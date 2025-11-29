-- Test data for integration tests

-- Create tables
CREATE TABLE IF NOT EXISTS country (
    Code char(3) NOT NULL DEFAULT '',
    Name char(52) NOT NULL DEFAULT '',
    Continent enum('Asia','Europe','North America','Africa','Oceania','Antarctica','South America') NOT NULL DEFAULT 'Asia',
    Region char(26) NOT NULL DEFAULT '',
    SurfaceArea decimal(10,2) NOT NULL DEFAULT '0.00',
    IndepYear smallint DEFAULT NULL,
    Population int NOT NULL DEFAULT '0',
    LifeExpectancy decimal(3,1) DEFAULT NULL,
    GNP decimal(10,2) DEFAULT NULL,
    GNPOld decimal(10,2) DEFAULT NULL,
    LocalName char(45) NOT NULL DEFAULT '',
    GovernmentForm char(45) NOT NULL DEFAULT '',
    HeadOfState char(60) DEFAULT NULL,
    Capital int DEFAULT NULL,
    Code2 char(2) NOT NULL DEFAULT '',
    PRIMARY KEY (Code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS city (
    ID int NOT NULL AUTO_INCREMENT,
    Name char(35) NOT NULL DEFAULT '',
    CountryCode char(3) NOT NULL DEFAULT '',
    District char(20) NOT NULL DEFAULT '',
    Population int NOT NULL DEFAULT '0',
    PRIMARY KEY (ID),
    KEY CountryCode (CountryCode),
    CONSTRAINT city_ibfk_1 FOREIGN KEY (CountryCode) REFERENCES country (Code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Insert sample test data
INSERT INTO country (Code, Name, Continent, Region, Population, Capital) VALUES
    ('USA', 'United States', 'North America', 'North America', 331000000, 1),
    ('CHN', 'China', 'Asia', 'Eastern Asia', 1440000000, 2),
    ('JPN', 'Japan', 'Asia', 'Eastern Asia', 126000000, 3),
    ('DEU', 'Germany', 'Europe', 'Western Europe', 83000000, 4),
    ('GBR', 'United Kingdom', 'Europe', 'British Islands', 68000000, 5);

INSERT INTO city (ID, Name, CountryCode, District, Population) VALUES
    (1, 'Washington', 'USA', 'District of Columbia', 700000),
    (2, 'Beijing', 'CHN', 'Beijing', 21540000),
    (3, 'Tokyo', 'JPN', 'Tokyo', 13960000),
    (4, 'Berlin', 'DEU', 'Berlin', 3645000),
    (5, 'London', 'GBR', 'England', 8982000),
    (6, 'New York', 'USA', 'New York', 8419000),
    (7, 'Shanghai', 'CHN', 'Shanghai', 27058000),
    (8, 'Osaka', 'JPN', 'Osaka', 19281000),
    (9, 'Munich', 'DEU', 'Bavaria', 1472000),
    (10, 'Manchester', 'GBR', 'England', 547000);
