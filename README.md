# Two-Phase-Merge-Sort-
Implementation of External Sort. Details can be referred from README.

Implementation of two phase merge sort algorithm to sort large number of records with the specifications mentioned below : 

1. Metadata information: 
a. This information will be provided in a file as an input to  algorithm and will contain the information about the columns and their data type.
b. The number of columns can vary from 1 to 20. 
c. The data type for the column can be String, Integer or Date. 
i. Date: It will be represent by date. e.g. col1,date or mycol,date etc. 
ii. String:It will be represented by char(<length of string>) i.e. every string will be of same size defined by <length of string> e.g. cols,char(10) or column,char(11) etc. 
iii. Integer: e.g.  It will be represented by int. col1,int or mycol,int etc. 

2. Input Data: 
a. Each record will start from newline. 
b. Column values will be separated by comma. 
c. String will comprise of alphanumeric characters only i.e. A-Z a-z 0-9 
d. Date will be in the format DD/MM/YYYY format.

3. Output Data: 
a. Output file will contain the columns that you have specified as an argument to your algorithm. So, the number of columns can differ in output file as compared to input file. 
b. Each record will be separated by new line and column values by comma(similar to input data format). 

4. Main memory: 
a. The main memory that is allowed to use will be specified by command line argument. 
b. Checked if the two phase merge sort is feasible or not in the provided memory as argument. If not, proper error message is generated. 
c. For C/C++, use the dynamic memory allocation malloc and request 80% of the main memory specified in MB from the underlying OS and use the same memory for sorting. 
d. For Java, use the argument -Xmx to restrict the heap size equal to 10 times memory specified in MB. 

5. Sorting columns: 
a. If the sorting needs to be done using multiple columns then if the values of the first column is same,sorting on the basis of the second column and so on is done. 
b. If the value of all the columns mentioned for sorting is same, for such records the order in the output should be maintained as the order in the input file. 
