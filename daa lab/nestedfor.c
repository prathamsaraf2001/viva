#include<stdio.h>
#include <time.h>

void swap(int *xp, int *yp)
{
    int temp = *xp;
    *xp = *yp;
    *yp = temp;
}

void bubbleSort(int arr[], int n)
{
   int i, j;
   for (i = 0; i < n-1; i++)      
  
       // Last i elements are already in place   
       for (j = 0; j < n-i-1; j++) 
           if (arr[j] > arr[j+1])
              swap(&arr[j], &arr[j+1]);
}

void printArray(int arr[], int size)
{
    int i;
    for (i=0; i < size; i++)
        printf("%d ", arr[i]);
    printf("\n");
}   

void printMaxMin(int arr[], int size)
{
    
    printf("%d ", arr[0]);
    printf("%d ", arr[size-1]);
    printf("\n");
}   


int main()
{
    clock_t start_t, end_t, total_t;
	int arr[] = {64, 34, 25, 12, 22, 11, 90};
	int n = sizeof(arr)/sizeof(arr[0]);
     start_t = clock();
	bubbleSort(arr, n);
    end_t = clock();
	printf("max and min is:");
	printMaxMin(arr, n);
    total_t = (double)(end_t - start_t) / CLOCKS_PER_SEC;
    printf("\nTotal time taken by CPU: %f\n", total_t  );
	return 0;
}