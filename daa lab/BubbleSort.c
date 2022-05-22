#include <stdio.h>
void bubbleSort(int array[], int size) {
for (int step = 0; step < size - 1; ++step) {
for (int i = 0; i < size - step - 1; ++i) {
if (array[i] > array[i + 1]) {    //-----O(n^2) -Nested Loops
        int temp = array[i];
        array[i] = array[i + 1];
        array[i + 1] = temp;
      }
    }
  }
}
void printArray(int array[], int size) {
  for (int i = 0; i < size; ++i) {
    printf("%d  ", array[i]);   //-----O(n)
  }
  printf("\n");
}

int main() {
int data[] = {9, 6, 3, 2, 5};
int size = sizeof(data) / sizeof(data[0]);
bubbleSort(data, size);
printf("Sorted Array:\n");
printArray(data, size);
}

//Time complexity is O(n^2)
