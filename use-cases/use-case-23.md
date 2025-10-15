# USE CASE: 23 View Population Breakdown by Urban and Non-Urban for Each Continent

## CHARACTERISTIC INFORMATION

### Goal in Context
As a Data Analyst, I want to view the population of people, people living in cities, and people not living in cities for each continent so that I can understand urbanization trends per continent.

### Scope
The provided SQL database at https://dev.mysql.com/doc/index-other.html, specifically the tables containing population, city, and continent data.

### Level
Primary task, as this involves querying and aggregating population data for each continent.

### Preconditions
The SQL database is accessible and contains current population and city data.
The Data Analyst has the necessary permissions to query the database.

### Success End Condition
A breakdown of total population, urban population, and non-urban population for each continent is generated and displayed for the Data Analyst to review.

### Failed End Condition
No report is produced, such as due to database errors or lack of data.

### Primary Actor
Data Analyst (the user who initiates the query to view the report).

### Trigger
The Data Analyst requests a report for population breakdowns by urban and non-urban categories per continent, such as during urbanization trend analysis or in response to an organizational need.

## MAIN SUCCESS SCENARIO
Data Analyst initiates a request to view the population breakdown for each continent.
System queries the SQL database to retrieve total population data and city-based population data for all continents.
System calculates the urban population (population in cities) and non-urban population (total population minus urban population) for each continent.
System displays the results in a sorted or summarized format, including details such as continent name, total population, urban population, and non-urban population.
Data Analyst reviews the report to understand urbanization trends per continent.

## EXTENSIONS
2a. **Database query fails (e.g., due to connectivity issues)**:
    System displays an error message to the Data Analyst, and the process ends.

3a. **No population data available for one or more continents**:
    System notifies the Data Analyst of missing data and provides results for available continents only.

## SUB-VARIATIONS
None.

## SCHEDULE
**DUE DATE**: Release 1.0