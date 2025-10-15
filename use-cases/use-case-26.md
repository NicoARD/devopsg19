# USE CASE: 26 View Total World Population

## CHARACTERISTIC INFORMATION

### Goal in Context
As a Data Analyst, I want to view the total population of the world so that I can get a global overview.

### Scope
The provided SQL database at https://dev.mysql.com/doc/index-other.html, specifically the tables containing global population data.

### Level
Primary task, as this involves querying aggregate population data for the world.

### Preconditions
The SQL database is accessible and contains current global population data.
The Data Analyst has the necessary permissions to query the database.

### Success End Condition
The total world population is retrieved and displayed for the Data Analyst to review.

### Failed End Condition
No report is produced, such as due to database errors or lack of data.

### Primary Actor
Data Analyst (the user who initiates the query to view the report).

### Trigger
The Data Analyst requests a report for the total world population, such as during global overview analysis or in response to an organizational need.

## MAIN SUCCESS SCENARIO
Data Analyst initiates a request to view the total world population.
System queries the SQL database to retrieve the latest global population data.
System displays the total world population in a user-friendly format (e.g., a dashboard or report).
Data Analyst reviews the report for a global overview.

## EXTENSIONS
2a. **Database query fails (e.g., due to connectivity issues)**:
    System displays an error message to the Data Analyst, and the process ends.

3a. **No population data available**:
    System notifies the Data Analyst that no data was found and suggests checking the database.

## SUB-VARIATIONS
None.

## SCHEDULE
DUE DATE: Release 1.0