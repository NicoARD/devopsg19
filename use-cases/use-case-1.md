# USE CASE: 1 View All Countries by Population Ranking

## CHARACTERISTIC INFORMATION

### Goal in Context
As a Data Analyst, I want to view all countries in the world sorted by largest population to smallest so that I can analyze global population rankings.

### Scope
The provided SQL database at https://dev.mysql.com/doc/index-other.html, specifically the tables containing country and population data.

### Level
Primary task, as this involves directly querying and displaying population data for countries.

### Preconditions

The SQL database is accessible and contains current country population data.
The Data Analyst has the necessary permissions to query the database.

### Success End Condition
A sorted list of all countries by population (largest to smallest) is generated and displayed for the Data Analyst to review.

### Failed End Condition
No report is produced, such as due to database errors or lack of data.

### Primary Actor
Data Analyst (the user who initiates the query to view the report).

### Trigger
The Data Analyst requests a report on global country populations, such as during an analysis session or in response to an organizational need.

## MAIN SUCCESS SCENARIO
1. Data Analyst initiates a request to view all countries sorted by population.
2. Data Analyst selects the specific report option for "all countries by population ranking."
3. System queries the SQL database to retrieve all country data, including population.
4. System sorts the retrieved data by population in descending order.
  5. System displays the sorted list of countries, including details such as country name and population, in a user-friendly format (e.g., a table or list).
6. Data Analyst reviews the report for global population analysis.

## EXTENSIONS
3a. **Database query fails**:
    1. System displays an error message to the Data Analyst, indicating the failure, and the process ends.

3b. **No country data available**:
    1. System notifies the Data Analyst that no data was found and suggests checking the database or preconditions.

## SUB-VARIATIONS
None.

## SCHEDULE
**DUE DATE**: Release 1.0