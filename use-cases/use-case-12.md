# USE CASE: 12 View Top N Populated Cities in the World

## CHARACTERISTIC INFORMATION

### Goal in Context
As a Data Analyst, I want to view the top N populated cities in the world so that I can identify the largest urban centers globally.

### Scope
The provided SQL database at https://dev.mysql.com/doc/index-other.html, specifically the tables containing city and population data.

### Level
Primary task, as this involves querying, sorting, and limiting city data globally.

### Preconditions
The SQL database is accessible and contains current city and population data.
The Data Analyst knows the value of N (the number of top cities to retrieve).
The Data Analyst has the necessary permissions to query the database.

### Success End Condition
A sorted list of the top N populated cities in the world (by largest population) is generated and displayed for the Data Analyst to review.

### Failed End Condition
No report is produced, such as due to invalid N value, database errors, or lack of data.

### Primary Actor
Data Analyst (the user who initiates the query to view the report).

### Trigger
The Data Analyst requests a report for the top N populated cities in the world, such as during global urban analysis or in response to an organizational need.

## MAIN SUCCESS SCENARIO
Data Analyst initiates a request to view the top N populated cities in the world.
Data Analyst enters or selects the value of N (e.g., 10 for the top 10 cities).
System queries the SQL database to retrieve city data, including population.
System sorts the data by population in descending order and limits the results to the top N cities.
System displays the list of top N cities, including details such as city name and population, in a user-friendly format (e.g., a table or list).
Data Analyst reviews the report to identify the largest urban centers.

## EXTENSIONS
2a. **Invalid N value provided (e.g., N is not a positive integer)**:
    System displays an error message to the Data Analyst (e.g., "Invalid value for N"), and the process ends. Data Analyst can correct the input and retry.

3a. **Database query fails (e.g., due to connectivity issues)**:
    System notifies the Data Analyst of the error, and the process ends.

4a. **Fewer cities than N in the database**:
    System returns all available cities and informs the Data Analyst of the limitation.

## SUB-VARIATIONS
None.

## SCHEDULE
DUE DATE: Release 1.0