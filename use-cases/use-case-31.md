# USE CASE: 31 View Population of a Specific City

## CHARACTERISTIC INFORMATION

### Goal in Context
As a Data Analyst, I want to view the population of a specific city so that I can get detailed urban data.

### Scope
The provided SQL database at https://dev.mysql.com/doc/index-other.html, specifically the tables containing city and population data.

### Level
Primary task, as this involves querying population data for a specific city.

### Preconditions
The SQL database is accessible and contains current city and population data.
The Data Analyst knows the name of the specific city to query.
The Data Analyst has the necessary permissions to query the database.

### Success End Condition
The population of the specified city is retrieved and displayed for the Data Analyst to review.

### Failed End Condition
No report is produced, such as due to invalid city input, database errors, or lack of data.

### Primary Actor
Data Analyst (the user who initiates the query to view the report).

### Trigger
The Data Analyst requests a report for the population of a specific city, such as during urban data analysis or in response to an organizational need.

## MAIN SUCCESS SCENARIO
Data Analyst initiates a request to view the population of a specific city.
Data Analyst enters or selects the name of the specific city (e.g., "New York").
System queries the SQL database to retrieve the population data for the specified city.
System displays the population information in a user-friendly format (e.g., a report or dashboard).
Data Analyst reviews the report to get detailed urban data.

## EXTENSIONS
2a. **Invalid city name provided (e.g., non-existent city)**:
    System displays an error message to the Data Analyst (e.g., "City not found"), and the process ends. Data Analyst can correct the input and retry.
3a. **Database query fails (e.g., due to connectivity issues)**:
    System notifies the Data Analyst of the error, and the process ends.
4a. **No population data available for the city**:
    System informs the Data Analyst that no data was found and suggests verifying the input.

## SUB-VARIATIONS
None.

## SCHEDULE
**DUE DATE**: Release 1.0