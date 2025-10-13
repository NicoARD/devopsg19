-- Grant permissions to devuser for the world database
-- This script runs after world.sql due to alphabetical ordering (02- after 01-)

-- Grant all privileges on world database to devuser
GRANT ALL PRIVILEGES ON world.* TO 'devuser'@'%';

-- Flush privileges to ensure changes take effect
FLUSH PRIVILEGES;

-- Create a simple test to verify the database is accessible
USE world;
SELECT COUNT(*) as city_count FROM city LIMIT 1;
SELECT COUNT(*) as country_count FROM country LIMIT 1;
SELECT COUNT(*) as countrylanguage_count FROM countrylanguage LIMIT 1;