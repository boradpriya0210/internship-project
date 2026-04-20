-- Create databases if they don't exist
CREATE DATABASE IF NOT EXISTS user_service_db;
CREATE DATABASE IF NOT EXISTS skill_assessment_db;
CREATE DATABASE IF NOT EXISTS internship_service_db;
CREATE DATABASE IF NOT EXISTS notification_service_db;

-- Optional: Create a specific user with permissions (though using root is common for local dev)
-- CREATE USER 'intern_user'@'%' IDENTIFIED BY 'intern_pass';
-- GRANT ALL PRIVILEGES ON *.* TO 'intern_user'@'%';
-- FLUSH PRIVILEGES;
