#include <stdio.h>
#include <time.h>
 
void mergeSort(int [], int, int, int);
void partition(int [],int, int);
 
int main()
{
     clock_t start_t, end_t, total_t;
    int list[50];
    int i, size;
 
    printf("Enter total number of elements:");
    scanf("%d", &size);
    
    printf("Enter the elements:\n");
    for(i = 0; i < size; i++)
    {
         scanf("%d", &list[i]);
    }
     start_t = clock();
    partition(list, 0, size - 1);
    end_t = clock();
    printf("Smallest element is :\n");
    printf("%d   ",list[0]);
    printf("\nLargest element is :\n");
    printf("%d   ",list[size-1]);

    total_t = (double)(end_t - start_t) / CLOCKS_PER_SEC;
    printf("\nTotal time taken by CPU: %f\n", total_t  );
    
 
   return 0;
}
 
void partition(int list[],int low,int high)
{
    int mid;
    
    if(low < high)
    {
        mid = (low + high) / 2;
        partition(list, low, mid);
        partition(list, mid + 1, high);
        mergeSort(list, low, mid, high);
    }
    
}
 
void mergeSort(int list[],int low,int mid,int high)
{
    int i, mi, k, lo, temp[50];
 
    lo = low;
    i = low;
    mi = mid + 1;
    while ((lo <= mid) && (mi <= high))
    {
        if (list[lo] <= list[mi])
        {
            temp[i] = list[lo];
            lo++;
        }
        else
        {
            temp[i] = list[mi];
            mi++;
        }
        i++;
    }
    if (lo > mid)
    {
        for (k = mi; k <= high; k++)
        {
            temp[i] = list[k];
            i++;
        }
    }
    else
    {
        for (k = lo; k <= mid; k++)
        {
             temp[i] = list[k];
             i++;
        }
    }
 
    for (k = low; k <= high; k++)
    {
        list[k] = temp[k];
    }
}