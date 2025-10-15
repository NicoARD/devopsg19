# USE CASE: 9 View All Cities in a Specific Region by Population Ranking

## CHARACTERISTIC INFORMATION

### Goal in Context
As a Data Analyst, I want to view all cities in a specific region sorted by largest population to smallest so that I can compare city populations within a region.

### Scope
The provided SQL database at https://dev.mysql.com/doc/index-other.html, specifically the tables containing city, population, and region data.

### Level
Primary task, as this involves querying and sorting city data for a specific region.

### Preconditions
The SQL database is accessible and contains current city, population, and region data.
The Data Analyst knows the name of the specific region to query.
The Data Analyst has the necessary permissions to query the database.

### Success End Condition
A sorted list of all cities in the specified region by population (largest to smallest) is generated and displayed for the Data Analyst to review.

### Failed End Condition
No report is produced, such as due to invalid region input, database errors, or lack of data.

### Primary Actor
Data Analyst (the user who initiates the query to view the report).

### Trigger
The Data Analyst requests a report for all cities in a specific region sorted by population, such as during regional urban analysis or in response to an organizational need.

## MAIN SUCCESS SCENARIO
Data Analyst initiates a request to view all cities in a specific region sorted by population.
Data Analyst enters or selects the name of the specific region (e.g., "Western Europe").
System queries the SQL database to retrieve city data for the specified region, including population.
System sorts the retrieved data by population in descending order.
System displays the sorted list of cities, including details such as city name and population, in a user-friendly format (e.g., a table or list).
Data Analyst reviews the report to compare city populations within the region.

## EXTENSIONS
2a. **Invalid region name provided (e.g., non-existent region)**:
System displays an error message to the Data Analyst (e.g., "Region not found"), and the process ends. Data Analyst can correct the input and retry.
3a. **Database query fails (e.g., due to connectivity issues)**:
System notifies the Data Analyst of the error, and the process ends.
4a. **No city data available for the region**:
System informs the Data Analyst that no data was found and suggests verifying the input.

## SUB-VARIATIONS
None.

## SCHEDULE
DUE DATE: Release 1.0