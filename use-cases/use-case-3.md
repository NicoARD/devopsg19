# USE CASE: 3 View Countries in a Region by Population Ranking

## CHARACTERISTIC INFORMATION

### Goal in Context
As a Data Analyst, I want to view all countries in a specific region sorted by largest population to smallest so that I can compare populations within a region.

### Scope
The provided SQL database at https://dev.mysql.com/doc/index-other.html, specifically the tables containing country, population, and region data.

### Level
Primary task, as this involves querying and sorting country data for a specific region.

### Preconditions
The SQL database is accessible and contains current country and population data.
The Data Analyst knows the name of the specific region to query.
The Data Analyst has the necessary permissions to access the database.

### Success End Condition
A sorted list of all countries in the specified region by population (largest to smallest) is generated and displayed for the Data Analyst to review.

### Failed End Condition
No report is produced, such as due to database errors, invalid region input, or lack of data.

### Primary Actor
Data Analyst (the user who initiates the query to view the report).

### Trigger
The Data Analyst requests a report for countries in a specific region, such as during regional analysis or in response to an organizational request.

## MAIN SUCCESS SCENARIO

Data Analyst initiates a request to view countries in a specific region sorted by population.
Data Analyst enters or selects the name of the specific region (e.g., "Western Europe").
System queries the SQL database to retrieve all country data for the specified region, including population.
System sorts the retrieved data by population in descending order.
System displays the sorted list of countries, including details such as country name, population, and region, in a user-friendly format (e.g., a table or list).
Data Analyst reviews the report to compare populations within the region.

## EXTENSIONS
2a. **Invalid region name provided (e.g., non-existent region)**:
    1. System displays an error message to the Data Analyst (e.g., "Region not found"), and the process ends. Data Analyst can correct the input and retry.

3a. **Database query fails (e.g., due to connectivity issues)**:
    1.System notifies the Data Analyst of the error, and the process ends.
3b. **No countries found in the region**:
    1.System informs the Data Analyst that no data was found for the region and suggests verifying the input.

## SUB-VARIATIONS
None.

##SCHEDULE
**DUE DATE**: Release 1.0

