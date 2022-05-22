#include <stdio.h>
#include <time.h>

void create(int[]);
void down_adjust(int[], int);

void main()
{
  clock_t start_t, end_t, total_t;
  int heap[30], n, i, last, temp;
  start_t = clock();
  printf("Enter no. of elements:");
  scanf("%d", &n);
  printf("\nEnter elements:");
  for (i = 1; i <= n; i++)
    scanf("%d", &heap[i]);
  heap[0] = n;
  create(heap);
  // sorting
  while (heap[0] > 1)
  {
    last = heap[0];
    temp = heap[1];
    heap[1] = heap[last];
    heap[last] = temp;
    heap[0]--;
    down_adjust(heap, 1);
  }

  printf("\nArray after sorting:\n");
  for (i = 1; i <= n; i++)
    printf("%d ", heap[i]);

    end_t = clock();
  total_t = (double)(end_t - start_t) / CLOCKS_PER_SEC;
  printf("\nTotal time taken by CPU: %f\n", total_t);
}

void create(int heap[])
{
  int i, n;
  n = heap[0]; 
  for (i = n / 2; i >= 1; i--)
    down_adjust(heap, i);
}

void down_adjust(int heap[], int i)
{
  int j, temp, n, flag = 1;
  n = heap[0];
  while (2 * i <= n && flag == 1)
  {
    j = 2 * i; // j points to left child
    if (j + 1 <= n && heap[j + 1] > heap[j])
      j = j + 1;
    if (heap[i] > heap[j])
      flag = 0;
    else
    {
      temp = heap[i];
      heap[i] = heap[j];
      heap[j] = temp;
      i = j;
    }
  }
  
}