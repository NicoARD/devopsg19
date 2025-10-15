# USE CASE: 30 View Population of a Specific District

## CHARACTERISTIC INFORMATION

### Goal in Context
As a Data Analyst, I want to view the population of a specific district so that I can understand local demographics.

### Scope
The provided SQL database at https://dev.mysql.com/doc/index-other.html, specifically the tables containing district and population data.

### Level
Primary task, as this involves querying population data for a specific district.

### Preconditions
The SQL database is accessible and contains current district and population data.
The Data Analyst knows the name of the specific district to query.
The Data Analyst has the necessary permissions to query the database.

### Success End Condition
The population of the specified district is retrieved and displayed for the Data Analyst to review.

### Failed End Condition
No report is produced, such as due to invalid district input, database errors, or lack of data.

### Primary Actor
Data Analyst (the user who initiates the query to view the report).

### Trigger
The Data Analyst requests a report for the population of a specific district, such as during local demographic analysis or in response to an organizational need.

## MAIN SUCCESS SCENARIO
Data Analyst initiates a request to view the population of a specific district.
Data Analyst enters or selects the name of the specific district (e.g., "Manhattan").
System queries the SQL database to retrieve the population data for the specified district.
System displays the population information in a user-friendly format (e.g., a report or dashboard).
Data Analyst reviews the report to understand local demographics.

## EXTENSIONS
2a. **Invalid district name provided (e.g., non-existent district)**:
    System displays an error message to the Data Analyst (e.g., "District not found"), and the process ends. Data Analyst can correct the input and retry.

3a. **Database query fails (e.g., due to connectivity issues)**:
    System notifies the Data Analyst of the error, and the process ends.

4a. **No population data available for the district**:
    System informs the Data Analyst that no data was found and suggests verifying the input.

## SUB-VARIATIONS
None.

## SCHEDULE
DUE DATE: Release 1.0