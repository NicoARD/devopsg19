# USE CASE: 32 View Top Language Speakers by Population with World Percentage

## CHARACTERISTIC INFORMATION

### Goal in Context
As a Data Analyst, I want to view the number of people speaking Chinese, English, Hindi, Spanish, or Arabic, sorted from greatest to smallest, including the percentage of the world population, so that I can understand global language distributions.

### Scope
The provided SQL database at https://dev.mysql.com/doc/index-other.html, specifically the tables containing language, speaker population, and world population data.

### Level
Primary task, as this involves querying, sorting, and calculating percentage data for specific languages.

### Preconditions
The SQL database is accessible and contains current language speaker and world population data.
The Data Analyst has the necessary permissions to query the database.

### Success End Condition
A sorted list of the top speakers for the specified languages (Chinese, English, Hindi, Spanish, Arabic), including speaker numbers and world population percentages, is generated and displayed for the Data Analyst to review.

### Failed End Condition
No report is produced, such as due to database errors or lack of data.

### Primary Actor
Data Analyst (the user who initiates the query to view the report).

### Trigger
The Data Analyst requests a report for the top language speakers with percentages, such as during global language distribution analysis or in response to an organizational need.

## MAIN SUCCESS SCENARIO
Data Analyst initiates a request to view the top speakers for the specified languages.
System queries the SQL database to retrieve speaker population data for Chinese, English, Hindi, Spanish, and Arabic.
System calculates the percentage of the world population for each language based on total world population data.
System sorts the results by speaker population in descending order.
System displays the sorted list, including details such as language name, number of speakers, and percentage of world population, in a user-friendly format (e.g., a table or chart).
Data Analyst reviews the report to understand global language distributions.

## EXTENSIONS
2a. **Database query fails (e.g., due to connectivity issues)**:
    System displays an error message to the Data Analyst, and the process ends.
3a. **No language data available**:
    System notifies the Data Analyst that no data was found and suggests checking the database.
4a. **Incomplete data for languages**:
    System returns available data and indicates missing entries.

## SUB-VARIATIONS
None.

## SCHEDULE
DUE DATE: Release 1.0