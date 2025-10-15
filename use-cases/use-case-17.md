# USE CASE: 17 View All Capital Cities in the World by Population Ranking

## CHARACTERISTIC INFORMATION

### Goal in Context
As a Data Analyst, I want to view all capital cities in the world sorted by largest population to smallest so that I can compare global capitals.

### Scope
The provided SQL database at https://dev.mysql.com/doc/index-other.html, specifically the tables containing capital cities, population, and related data.

### Level
Primary task, as this involves querying and sorting capital city data globally.

### Preconditions
The SQL database is accessible and contains current capital cities and population data.
The Data Analyst has the necessary permissions to query the database.

### Success End Condition
A sorted list of all capital cities in the world by population (largest to smallest) is generated and displayed for the Data Analyst to review.

### Failed End Condition
No report is produced, such as due to database errors or lack of data.

### Primary Actor
Data Analyst (the user who initiates the query to view the report).

### Trigger
The Data Analyst requests a report for all capital cities sorted by population, such as during global capital analysis or in response to an organizational need.

### MAIN SUCCESS SCENARIO
Data Analyst initiates a request to view all capital cities sorted by population.
System queries the SQL database to retrieve all capital city data, including population.
System sorts the retrieved data by population in descending order.
System displays the sorted list of capital cities, including details such as city name and population, in a user-friendly format (e.g., a table or list).
Data Analyst reviews the report to compare global capitals.

## EXTENSIONS
2a. **Database query fails (e.g., due to connectivity issues)**:
    System displays an error message to the Data Analyst, and the process ends.

3a. **No capital city data available**:
    System notifies the Data Analyst that no data was found and suggests checking the database.

## SUB-VARIATIONS
None.

## SCHEDULE
**DUE DATE**: Release 1.0