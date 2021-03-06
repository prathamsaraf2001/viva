import java.io.IOException;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.nio.file.Files;
import java.nio.file.Paths;

public class java_solverSISR {

    private static int m=1;

    public static void main(String[] args) throws IOException {
        double[][] data = Util_SimpleQuestionReader(args[0]);
        int capacity = Integer.parseInt(args[1]);
        int n_iter = Integer.parseInt(args[2]);

        ArrayList<int[]> routes = solveSISR_VRP(data, capacity, n_iter);
        double distance = getRoutesDistance(routes, getDistanceMatrix(data));
        StringBuilder sb = new StringBuilder();
        sb.append(distance);
        sb.append(":");
        for (int[] r : routes) {
            for (int i : r) {
                sb.append(i);
                sb.append(",");
            }
            sb.deleteCharAt(sb.length()-1);
            sb.append(";");
        }
        sb.deleteCharAt(sb.length()-1);
        System.out.println(sb.toString());
    }

    public static double[][] Util_SimpleQuestionReader(String path) throws IOException {
        String content = new String(Files.readAllBytes(Paths.get(path)), "UTF-8");
        String[] lines = content.split("\n");
        double[][] data = new double[lines.length][6];
        for (int i = 0 ; i<lines.length ; i++) {
            String[] d = lines[i].split("\\s+");
            for (int j = 0 ; j<d.length ; j++) {
                data[i][j] = Double.parseDouble(d[j]);
            }
        }
        return data;
    }

    private static int indexOfMin(double[] a) {
        int loc = 0;
        double min = a[loc];
        for (int i = 1; i < a.length; i++) {
            if (a[i] < min) {
                min = a[i];
                loc = i;
            }
        }
        return loc;
    }

    public static void sortRoutes(ArrayList<int[]> routes) {
        Collections.sort(routes, new Comparator<int[]>() {
            public int compare(int[] content0, int[] content1) {
                if (content0[0]<content1[0]) {
                    return -1;
                } else if (content0[0]>content1[0]) {
                    return 1;
                }
                return 0;
            }
        });
    }

    public static boolean compareRoutes(ArrayList<int[]> routesA, ArrayList<int[]> routesB) {
        if (routesA.size() != routesB.size()) return false;

        ArrayList<int[]> routesAReformat = new ArrayList<>();
        ArrayList<int[]> routesBReformat = new ArrayList<>();

        for (int i = 0 ; i<routesA.size() ; i++) {
            HashSet<Integer> set1 = new HashSet<Integer>(Arrays.stream(routesA.get(i)).boxed().collect(Collectors.toList()));
            HashSet<Integer> set2 = new HashSet<Integer>(Arrays.stream(routesB.get(i)).boxed().collect(Collectors.toList()));
            routesAReformat.add(set1.stream().mapToInt(Number::intValue).toArray());
            routesBReformat.add(set2.stream().mapToInt(Number::intValue).toArray());
        }
        sortRoutes(routesAReformat);
        sortRoutes(routesBReformat);
        for (int i = 0 ; i<routesAReformat.size() ; i++) {
            if (routesAReformat.get(i).length!=routesBReformat.get(i).length) {
                System.out.println(" -- Route A: "+java.util.Arrays.toString(routesAReformat.get(i)));
                System.out.println(" -- Route B: "+java.util.Arrays.toString(routesBReformat.get(i)));
                return false;
            }
            for (int j = 0 ; j<routesAReformat.get(i).length ; j++) {
                if (routesAReformat.get(i)[j] != routesBReformat.get(i)[j]) {
                    System.out.println(" -- Route A: "+java.util.Arrays.toString(routesAReformat.get(i)));
                    System.out.println(" -- Route B: "+java.util.Arrays.toString(routesBReformat.get(i)));
                    return false;
                }
            }
        }
        return true;
    }

    public static double getDistance(double x1, double y1, double x2, double y2) {
        return Math.sqrt(Math.pow(x1-x2,2) + Math.pow(y1-y2,2));
    }

    // 0 is not included in the route but will be calculated.
    public static double getRouteDistance(int[] route, double[][] distance_matrix) {
        int last_node = 0;
        double distance = 0.0;
        for (int c : route) {
            distance += distance_matrix[last_node][c];
            last_node = c;
        }
        distance += distance_matrix[last_node][0];
        return distance;
    }

    // 0 is not included in routes but will be calculated.
    public static double getRoutesDistance(ArrayList<int[]> routes, double[][] distance_matrix) {
        double total_distance = 0.0;
        for (int[] r : routes) {
            total_distance += getRouteDistance(r, distance_matrix);
        }
        return total_distance;
    }

    public static double[][] getDistanceMatrix(double[][] data) {
        double[][] distance_matrix = new double[data.length][data.length];
        for (int i = 0 ; i<data.length ; i++) {
            for (int j = 0 ; j<data.length ; j++) {
                if (i!=j) distance_matrix[i][j] = getDistance(data[i][0], data[i][1], data[j][0], data[j][1]);
            }
        }
        return distance_matrix;
    }

    private static int[][] buildNeighbours(double[][] distance_matrix) {
        int[][] neighbours = new int[distance_matrix.length][distance_matrix.length];
        for (int i = 0 ; i<distance_matrix.length ; i++) {
            java_Util_ArrayIndexComparator comparator = new java_Util_ArrayIndexComparator(distance_matrix[i], false);
            Integer[] indexes = comparator.createIndexArray();
            Arrays.sort(indexes, comparator);
            neighbours[i] = Arrays.stream(indexes).mapToInt(Integer::intValue).toArray();
        }
        return neighbours;
    }

    private static int find_r(ArrayList<int[]> lastRoute, int c) {
        int index = -1;
        for (int i = 0 ; i<lastRoute.size() ; i++) {
            if (Arrays.stream(lastRoute.get(i)).anyMatch(j -> j == c)) {
                index = i;
                break;
            }
        }
        return index;
    }

    private static int[] string_remove(int[] r, int l_t, int c) {
        int i_c = Arrays.stream(r).boxed().collect(Collectors.toList()).indexOf(c);
        int range1 = Math.max(0, i_c+1-l_t);
        int range2 = Math.min(i_c, r.length-l_t)+1;
        int start = ThreadLocalRandom.current().nextInt(range1, range2);
        return Arrays.copyOfRange(r, start, start+l_t);
    }

    private static int[] split_remove(int[] r, int l_t, int c) {
        int additional_l = Math.min(m, r.length-l_t);
        int l_t_m = l_t+additional_l;
        int i_c = Arrays.stream(r).boxed().collect(Collectors.toList()).indexOf(c);
        int range1 = Math.max(0, i_c+1-l_t_m);
        int range2 = Math.min(i_c, r.length-l_t_m)+1;
        int start = ThreadLocalRandom.current().nextInt(range1, range2);
        List<Integer> potential_removal = new ArrayList<Integer>(l_t_m);
        for (int i : Arrays.copyOfRange(r, start, start+l_t_m)) potential_removal.add(i);
        Collections.shuffle(potential_removal);
        return potential_removal.subList(0, l_t).stream().mapToInt(i->i).toArray();
    }

    private static int[] remove_nodes(int[] r, int l_t, int c, double m_alpha) {
        int[] removed;
        if (Math.random()<0.5) {
            removed = string_remove(r, l_t, c);
        } else {
            removed = split_remove(r, l_t, c);
            if (m<(r.length-l_t) || Math.random()>m_alpha) m++;
        }
        return removed;
    }

    private static ArrayList<Integer> ruin_getAbsents(ArrayList<int[]> lastRoute, int[][] neighbours, ArrayList<Integer> absents,
                                                      double c_bar, double L_max, double m_alpha) {
        m = 1;
        double mean_route_length = 0;
        for (int[] r : lastRoute) mean_route_length+=r.length;
        mean_route_length/=lastRoute.size();
        double l_s_max = Math.min(L_max, mean_route_length);
        double k_s_max = 4.0*c_bar/(1.0+l_s_max)-1.0;
        int k_s = (int)(Math.random()*k_s_max+1.0);
        int c_seed = (int)(Math.random()*neighbours.length);

        if (absents==null) absents = new ArrayList<Integer>();
        ArrayList<Integer> ruined_r = new ArrayList<Integer>();

        for (int c : neighbours[c_seed]) {
            if (ruined_r.size() >= k_s) break;
            if (c!=0 && !absents.contains(c)) {
                int r_index = find_r(lastRoute, c);
                if (!ruined_r.contains(r_index)) {
                    double l_t_max = Math.min(l_s_max, lastRoute.get(r_index).length);
                    int l_t = (int)(Math.random()*l_t_max+1.0);
                    int[] newly_removed = remove_nodes(lastRoute.get(r_index), l_t, c, m_alpha);
                    for (int _tmp_node : newly_removed) absents.add(_tmp_node);
                    ruined_r.add(r_index);
                }
            }
        }
        return absents;
    }

    private static ArrayList<int[]> ruin_routeSummary(ArrayList<int[]> lastRoute, ArrayList<Integer> absents) {
        ArrayList<int[]> absentRoute = new ArrayList<int[]>();
        for (int i = 0 ; i<lastRoute.size() ; i++) {
            ArrayList<Integer> r = new ArrayList<>();
            for (int c : lastRoute.get(i)) {
                if (!absents.contains(c)) r.add(c);
            }
            if (r.size()>0) {
                absentRoute.add(r.stream().mapToInt(j->j).toArray());
            }
        }
        return absentRoute;
    }

    private static int[] insertNode(int[] old_r, int pos, int c) {
        int[] new_r = new int[old_r.length+1];
        for (int i = 0 ; i<new_r.length ; i++) {
            if (i<pos) {
                new_r[i] = old_r[i];
            } else if (i>pos) {
                new_r[i] = old_r[i-1];
            } else {
                new_r[i] = c;
            }
        }
        return new_r;
    }

    private static boolean checkValid(double[][] data, double[][] distance_matrix, int[] r, int c) {
        double time_current = 0;
        int curr_node = 0;
        for (int i = 0 ; i<(r.length+1) ; i++) {
            int next_node = i==r.length?0:r[i];
            time_current+=distance_matrix[curr_node][next_node];
            time_current=Math.max(data[next_node][3], time_current);
            if (time_current<=data[next_node][4]) {
                time_current+=data[next_node][5];
            } else {
                return false;
            }
            curr_node = i==r.length?0:r[i];
        }
        return true;
    }

    private static ArrayList<double[]> getValid(double[][] data, double[][] distance_matrix, int[] r, int c) {
        ArrayList<double[]> valids = new ArrayList<>();
        double dist = getRouteDistance(r, distance_matrix);
        double tmp_time = 0;
        int curr_node = 0;
        for (int i = 0 ; i<(r.length+1) ; i++) {
            int next_node = i==r.length?0:r[i];
            tmp_time = Math.max(tmp_time, data[curr_node][3]);
            tmp_time+=data[curr_node][5];
            if (tmp_time+distance_matrix[curr_node][c]>data[c][4]) break;
            int[] new_r = insertNode(r, i, c);
            if (checkValid(data, distance_matrix, new_r, c)) {
                double new_dist = getRouteDistance(new_r, distance_matrix);
                valids.add(new double[]{i, new_dist-dist});
            }
            tmp_time+=distance_matrix[curr_node][next_node];
            curr_node = i==r.length?0:r[i];
        }
        return valids;
    }

    private static ArrayList<int[]> route_add(ArrayList<int[]> absent_route, int c, double[] adding_pos) {
        if (adding_pos[0]==-1) {
            absent_route.add(new int[]{c});
            return absent_route;
        }
        int[] new_r = insertNode(absent_route.get((int)adding_pos[0]), (int)adding_pos[1], c);
        absent_route.set((int)adding_pos[0], new_r);
        return absent_route;
    }

    private static ArrayList<int[]> recreate(double[][] data, double capcity, double[][] distance_matrix,
                                             ArrayList<int[]> absent_route, ArrayList<Integer> absents, int lastLength) {
        // ??????II
        Collections.shuffle(absents);
        ArrayList<Integer> newAbsents = new ArrayList<>();
        ArrayList<Integer> toKeep = new ArrayList<>();
        ArrayList<int[]> current_route = absent_route;
        for (int i = 0 ; i<absents.size() ; i++) {
            int c = absents.get(i);
            ArrayList<double[]> probablePlace = new ArrayList<>();
            for (int ir = 0 ; ir<absent_route.size() ; ir++) {
                int[] r = absent_route.get(ir);
                double dmd_sum = 0;
                for (int _tmp_node : r) dmd_sum+=data[_tmp_node][2];
                if ((dmd_sum+data[c][2])>capcity) continue;
                // all possible int values can round-trip to a double safely.
                ArrayList<double[]> valids = getValid(data, distance_matrix, r, c);
                for (double[] v : valids) probablePlace.add(new double[]{ir,v[0],v[1]});
            }
            double[] adding_pos = new double[]{-1,-1,-1};
            if (probablePlace.size()>0) {
                Collections.sort(probablePlace, new Comparator<double[]>() {
                    public int compare(double[] content0, double[] content1) {
                        if (content0[2]<content1[2]) {
                            return -1;
                        } else if (content0[2]>content1[2]) {
                            return 1;
                        }
                        return 0;
                    }
                });
                adding_pos = probablePlace.get(0);
            } else if (lastLength>0 && lastLength<=current_route.size()) {
                toKeep.add(i);
                continue;
            }
            current_route = route_add(current_route, c, adding_pos);
        }
        for (int i : toKeep) newAbsents.add(absents.get(i));
        absents.clear();
        for (int i : newAbsents) absents.add(i);
        return current_route;
    }

    public static ArrayList<int[]> fleetSISR_VRP(double[][] data, double cap, int n_iter, ArrayList<int[]> init_route,
                                                 double c_bar, double L_max, double m_alpha, int verbose_step) {
        // Calculate the distance matrix.
        double[][] distance_matrix = getDistanceMatrix(data);

        // The 0 point should not be included in the routes.
        ArrayList<int[]> best_route;
        if (init_route == null) {
            best_route = new ArrayList<int[]>();
            for (int i = 1 ; i<data.length ; i++) {
                best_route.add(new int[]{i});
            }
        } else {
            best_route = init_route;
        }
        if (n_iter<=0) return best_route;
        ArrayList<int[]> last_route = best_route;
        int[][] neighbours = buildNeighbours(distance_matrix);
        ArrayList<Integer> absents = new ArrayList<>();
        // No need to count the number of appearance of the depot point. Be careful to the indices!
        int[] absent_count = new int[data.length-1];

        for (int i_iter = 0 ; i_iter<n_iter ; i_iter++) {
            ArrayList<Integer> newAbsents = new ArrayList<>();
            for (int node : absents) newAbsents.add(node);
            // Ruin ??????I
            newAbsents = ruin_getAbsents(last_route, neighbours, newAbsents, c_bar, L_max, m_alpha);
            // Remove nodes.
            ArrayList<int[]> absent_route = ruin_routeSummary(last_route, newAbsents);
            // Recreate
            ArrayList<int[]> current_route = recreate(data, cap, distance_matrix, absent_route, newAbsents, last_route.size());
            // Check and update
            if (newAbsents.size()==0) {
                best_route = current_route;
                absents = new ArrayList<>();
                double[] routeAbsentCounts = new double[best_route.size()];
                for (int i = 0 ; i<best_route.size() ; i++) {
                    int[] r = best_route.get(i);
                    double count = 0.0;
                    for (int node : r) count+=absent_count[node-1];
                    routeAbsentCounts[i] = count;
                }
                absents = new ArrayList<>();
                for (int i : best_route.get(indexOfMin(routeAbsentCounts))) absents.add(i);
                last_route = ruin_routeSummary(best_route, absents);
            } else {
                int count_sum_new = 0;
                int count_sum_old = 0;
                for (int node : newAbsents) count_sum_new+=absent_count[node-1];
                for (int node : absents) count_sum_old+=absent_count[node-1];
                if (newAbsents.size()<absents.size() || count_sum_new<count_sum_old) {
                    last_route = current_route;
                    absents = newAbsents;
                }
                for (int node : newAbsents) absent_count[node-1]++;
            }

            if (verbose_step>0 && ((i_iter+1)%verbose_step==0 || (i_iter+1)==n_iter)) {
                System.out.println("# iter: "+(i_iter+1)+"/"+n_iter);
                System.out.println("    --fleet route: "+best_route.size()+", "+getRoutesDistance(best_route, distance_matrix));
            }
        }

        return best_route;
    }

    public static ArrayList<int[]> solveSISR_VRP(double[][] data, int capacity, int n_iter) {
        return solveSISR_VRP(data, capacity, n_iter, null, 100.0f, 1.0f, 10.0f, 10.0f, 0.01f, 0, 0);
    }

    // Data Format: XCOORD., YCOORD., DEMAND, READY TIME, DUE TIME, SERVICE TIME
    public static ArrayList<int[]> solveSISR_VRP(double[][] data, double cap, int n_iter, ArrayList<int[]> init_route,
                                                 double init_T, double final_T, double c_bar, double L_max, double m_alpha,
                                                 int verbose_step, double sol_distance) {
        // Calculate the distance matrix.
        double[][] distance_matrix = getDistanceMatrix(data);

        // The 0 point should not be included in the routes.
        ArrayList<int[]> best_route;
        if (init_route == null) {
            best_route = new ArrayList<int[]>();
            for (int i = 1 ; i<data.length ; i++) {
                best_route.add(new int[]{i});
            }
            // int fleetIter = Math.max(1, n_iter/10);
            // best_route = fleetSISR_VRP(data, cap, fleetIter, null, c_bar, L_max, m_alpha, 0);
        } else {
            best_route = init_route;
        }
        if (n_iter<=0) return best_route;
        double best_distance = getRoutesDistance(best_route, distance_matrix);
        ArrayList<int[]> last_route = best_route;
        double last_distance = best_distance;

        int[][] neighbours = buildNeighbours(distance_matrix);
        double alpha_T = Math.pow((final_T/init_T), (1.0/n_iter));
        double temperature = init_T;

        for (int i_iter = 0 ; i_iter<n_iter ; i_iter++) {
            // Ruin ??????I
            ArrayList<Integer> absents = ruin_getAbsents(last_route, neighbours, null, c_bar, L_max, m_alpha);
            // Remove nodes.
            ArrayList<int[]> absent_route = ruin_routeSummary(last_route, absents);
            // Recreate
            ArrayList<int[]> current_route = recreate(data, cap, distance_matrix, absent_route, absents, 0);
            // Check and update
            double current_distance = getRoutesDistance(current_route, distance_matrix);
            if (current_route.size()<last_route.size() ||
                    (current_route.size()==last_route.size() &&current_distance<(last_distance-temperature*Math.log(Math.random())))) {
                last_route = current_route;
                last_distance = current_distance;
                if (current_route.size()<best_route.size() || current_distance<best_distance) {
                    best_route = current_route;
                    best_distance = current_distance;
                }
            }
            temperature*=alpha_T;

            if (verbose_step>0 && ((i_iter+1)%verbose_step==0 || (i_iter+1)==n_iter)) {
                System.out.println("# iter: "+(i_iter+1)+"/"+n_iter);
                System.out.println("    --best route: "+best_route.size()+", "+best_distance);
                System.out.println("    --last route: "+last_route.size()+", "+last_distance);
                System.out.println("    --curr route: "+current_route.size()+", "+current_distance);
            }

            if (sol_distance>0 && best_distance<(sol_distance-1e-6)) {
                System.out.println("# iter: "+(i_iter+1)+"/"+n_iter);
                System.out.println("    --best route: "+best_route.size()+", "+best_distance);
                System.out.println("    --last route: "+last_route.size()+", "+last_distance);
                System.out.println("    --curr route: "+current_route.size()+", "+current_distance);
                break;
            }
        }

        return best_route;
    }

    public static ArrayList<Double> solveSISR_VRP_logs(double[][] data, int capacity, int n_iter) {
        return solveSISR_VRP_logs(data, capacity, n_iter, null, 100.0f, 1.0f, 10.0f, 10.0f, 0.01f, 0, 0);
    }

    // Data Format: XCOORD., YCOORD., DEMAND, READY TIME, DUE TIME, SERVICE TIME
    public static ArrayList<Double> solveSISR_VRP_logs(double[][] data, double cap, int n_iter, ArrayList<int[]> init_route,
                                                       double init_T, double final_T, double c_bar, double L_max, double m_alpha,
                                                       int verbose_step, double sol_distance) {
        // Calculate the distance matrix.
        double[][] distance_matrix = getDistanceMatrix(data);

        // The 0 point should not be included in the routes.
        ArrayList<int[]> best_route;
        ArrayList<Double> logs = new ArrayList<>();
        if (init_route == null) {
            best_route = new ArrayList<int[]>();
            for (int i = 1 ; i<data.length ; i++) {
                best_route.add(new int[]{i});
            }
        } else {
            best_route = init_route;
        }
        if (n_iter<=0) return logs;
        double best_distance = getRoutesDistance(best_route, distance_matrix);
        ArrayList<int[]> last_route = best_route;
        double last_distance = best_distance;

        int[][] neighbours = buildNeighbours(distance_matrix);
        double alpha_T = Math.pow((final_T/init_T), (1.0/n_iter));
        double temperature = init_T;

        for (int i_iter = 0 ; i_iter<n_iter ; i_iter++) {
            // Ruin ??????I
            ArrayList<Integer> absents = ruin_getAbsents(last_route, neighbours, null, c_bar, L_max, m_alpha);
            // Remove nodes.
            ArrayList<int[]> absent_route = ruin_routeSummary(last_route, absents);
            // Recreate
            ArrayList<int[]> current_route = recreate(data, cap, distance_matrix, absent_route, absents, 0);
            // Check and update
            double current_distance = getRoutesDistance(current_route, distance_matrix);
            logs.add(current_distance);
            if (current_route.size()<last_route.size() ||
                    (current_route.size()==last_route.size() &&current_distance<(last_distance-temperature*Math.log(Math.random())))) {
                last_route = current_route;
                last_distance = current_distance;
                if (current_route.size()<best_route.size() || current_distance<best_distance) {
                    best_route = current_route;
                    best_distance = current_distance;
                }
            }
            temperature*=alpha_T;

            if (verbose_step>0 && ((i_iter+1)%verbose_step==0 || (i_iter+1)==n_iter)) {
                System.out.println("# iter: "+(i_iter+1)+"/"+n_iter);
                System.out.println("    --best route: "+best_route.size()+", "+best_distance);
                System.out.println("    --last route: "+last_route.size()+", "+last_distance);
                System.out.println("    --curr route: "+current_route.size()+", "+current_distance);
            }

            if (sol_distance>0 && best_distance<(sol_distance-1e-6)) {
                System.out.println("# iter: "+(i_iter+1)+"/"+n_iter);
                System.out.println("    --best route: "+best_route.size()+", "+best_distance);
                System.out.println("    --last route: "+last_route.size()+", "+last_distance);
                System.out.println("    --curr route: "+current_route.size()+", "+current_distance);
                break;
            }
        }

        return logs;
    }

}