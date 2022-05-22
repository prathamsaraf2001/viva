
#include <iostream>
#include <vector>
#include <algorithm>
using namespace std;

int maxBridges(vector<pair<int, int>> &values, int n)
{
    int memo[n];
    for (int i = 0; i < n; i++)
        memo[i] = 1;

    
    sort(values.begin(), values.end());


    int max = memo[0];

    
    for (int i = 1; i < n; i++)
    {
        for (int j = 0; j < i; j++)
        {
            if (values[i].second >= values[j].second && memo[i] < 1 + memo[j])
            {
                memo[i] = 1 + memo[j];
            }

            
            if (max < memo[i])
            {
                max = memo[i];
            }
        }
    }
    
    return max;
}

int main()
{
    int n, i;
    vector<int> north, south;
    int a;

    
    cout << "Enter the total number of cities on each bank: \n";
    cin >> n;
    cout << "Enter the coordinates of the cities on the northern bank: \n";
    for (i = 0; i < n; i++)
    {
        cin >> a;
        north.push_back(a);
    }

    cout << "Enter the coordinates of the cities on the southern bank:\n";
    for (i = 0; i < n; i++)
    {
        cin >> a;
        south.push_back(a);
    }

    
    vector<pair<int, int>> values;
    for (i = 0; i < n; i++)
    {
        values.push_back({south[i], north[i]});
    }

    
    cout << "The maximum number of bridges that can be constructed is " << maxBridges(values, n);
    return 0;
}
