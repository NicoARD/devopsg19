# USE CASE: 6 View Top N Populated Countries in a Specific Region

## CHARACTERISTIC INFORMATION

### Goal in Context
As a Data Analyst, I want to view the top N populated countries in a specific region so that I can focus on key countries in that region.

### Scope
The provided SQL database at https://dev.mysql.com/doc/index-other.html, specifically the tables containing country, population, and region data.

### Level
Primary task, as this involves querying, sorting, and limiting country data for a specific region.

### Preconditions
The SQL database is accessible and contains current country, population, and region data.
The Data Analyst knows the value of N (the number of top countries to retrieve) and the name of the specific region.
The Data Analyst has the necessary permissions to query the database.

### Success End Condition
A sorted list of the top N populated countries in the specified region (by largest population) is generated and displayed for the Data Analyst to review.

### Failed End Condition
No report is produced, such as due to invalid N value, invalid region input, database errors, or lack of data.

### Primary Actor
Data Analyst (the user who initiates the query to view the report).

### Trigger
The Data Analyst requests a report for the top N populated countries in a specific region, such as during regional analysis or in response to an organizational need.

## MAIN SUCCESS SCENARIO
Data Analyst initiates a request to view the top N populated countries in a specific region.
Data Analyst enters or selects the value of N (e.g., 5 for the top 5 countries) and the name of the region (e.g., "Western Europe").
System queries the SQL database to retrieve country data for the specified region, including population.
System sorts the data by population in descending order and limits the results to the top N countries.
System displays the list of top N countries, including details such as country name, population, and region, in a user-friendly format (e.g., a table or list).
Data Analyst reviews the report to focus on key countries in the region.

## EXTENSIONS
2a. **Invalid N value provided (e.g., N is not a positive integer)**:
    System displays an error message to the Data Analyst (e.g., "Invalid value for N"), and the process ends. Data Analyst can correct the input and retry.
2b. **Invalid region name provided (e.g., non-existent region)**:
    System displays an error message (e.g., "Region not found"), and the process ends. Data Analyst can correct the input and retry.
3a. **Database query fails (e.g., due to connectivity issues)**:
    System notifies the Data Analyst of the error, and the process ends.
4a. **Fewer countries than N in the region**:
    System returns all available countries in the region and informs the Data Analyst of the limitation.

## SUB-VARIATIONS
None.

## SCHEDULE
DUE DATE: Release 1.0