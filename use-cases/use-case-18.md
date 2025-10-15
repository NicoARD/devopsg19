# USE CASE: 18 View All Capital Cities in a Specific Continent by Population Ranking

## CHARACTERISTIC INFORMATION

### Goal in Context
As a Data Analyst, I want to view all capital cities in a specific continent sorted by largest population to smallest so that I can analyze capitals in that continent.

### Scope
The provided SQL database at https://dev.mysql.com/doc/index-other.html, specifically the tables containing capital cities, population, and continent data.

### Level
Primary task, as this involves querying and sorting capital city data for a specific continent.

### Preconditions
The SQL database is accessible and contains current capital cities, population, and continent data.
The Data Analyst knows the name of the specific continent to query.
The Data Analyst has the necessary permissions to query the database.

### Success End Condition
A sorted list of all capital cities in the specified continent by population (largest to smallest) is generated and displayed for the Data Analyst to review.

### Failed End Condition
No report is produced, such as due to invalid continent input, database errors, or lack of data.

### Primary Actor
Data Analyst (the user who initiates the query to view the report).

### Trigger
The Data Analyst requests a report for all capital cities in a specific continent sorted by population, such as during continental capital analysis or in response to an organizational need.

## MAIN SUCCESS SCENARIO
Data Analyst initiates a request to view all capital cities in a specific continent sorted by population.
Data Analyst enters or selects the name of the specific continent (e.g., "Africa").
System queries the SQL database to retrieve capital city data for the specified continent, including population.
System sorts the retrieved data by population in descending order.
System displays the sorted list of capital cities, including details such as city name and population, in a user-friendly format (e.g., a table or list).
Data Analyst reviews the report to analyze capitals in the continent.

## EXTENSIONS
2a. **Invalid continent name provided (e.g., non-existent continent)**:
    System displays an error message to the Data Analyst (e.g., "Continent not found"), and the process ends. Data Analyst can correct the input and retry.

3a. **Database query fails (e.g., due to connectivity issues)**:
    System notifies the Data Analyst of the error, and the process ends.

4a. **No capital city data available for the continent**:
    System informs the Data Analyst that no data was found and suggests verifying the input.

## SUB-VARIATIONS
None.

SCHEDULE
**DUE DATE**: Release 1.0