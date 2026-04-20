-- Sample Data for Internship Service

USE internship_service_db;

-- Insert Sample Internships
INSERT INTO internships (title, company, description, duration, stipend, location, type, created_at)
VALUES 
('Java Backend Developer Intern', 'TechCorp Solutions', 'Looking for a passionate Java developer to work on microservices architecture. You will be working with Spring Boot, MySQL, and cloud technologies.', 6, 15000.00, 'Bangalore', 'Hybrid', NOW()),
('Frontend React Developer', 'WebInnovate', 'Build modern, responsive web applications using React, JavaScript, and CSS. Great opportunity to learn from experienced developers.', 3, 12000.00, 'Mumbai', 'Remote', NOW()),
('Data Science Intern', 'DataMinds Analytics', 'Work on real-world data analysis projects using Python, machine learning, and statistical modeling. Prior experience with pandas and scikit-learn preferred.', 6, 18000.00, 'Pune', 'On-site', NOW()),
('Full Stack Development', 'StartupHub', 'Join our dynamic team to build end-to-end web applications. Work with Node.js, React, MongoDB, and modern development practices.', 4, 10000.00, 'Hyderabad', 'Hybrid', NOW()),
('Mobile App Developer', 'AppCrafters', 'Develop native Android applications using Java/Kotlin. Experience with Android Studio and REST APIs is a plus.', 5, 14000.00, 'Chennai', 'Remote', NOW());

-- Insert Required Skills for Java Backend Intern (ID = 1)
INSERT INTO required_skills (internship_id, skill_name, min_level, priority)
VALUES 
(1, 'Java', 'Intermediate', 5),
(1, 'Spring Boot', 'Beginner', 4),
(1, 'MySQL', 'Intermediate', 3),
(1, 'REST APIs', 'Beginner', 3);

-- Insert Required Skills for Frontend React Developer (ID = 2)
INSERT INTO required_skills (internship_id, skill_name, min_level, priority)
VALUES 
(2, 'JavaScript', 'Intermediate', 5),
(2, 'React', 'Beginner', 4),
(2, 'HTML', 'Intermediate', 3),
(2, 'CSS', 'Intermediate', 3);

-- Insert Required Skills for Data Science Intern (ID = 3)
INSERT INTO required_skills (internship_id, skill_name, min_level, priority)
VALUES 
(3, 'Python', 'Intermediate', 5),
(3, 'Machine Learning', 'Beginner', 4),
(3, 'Data Analysis', 'Intermediate', 4);

-- Insert Required Skills for Full Stack Development (ID = 4)
INSERT INTO required_skills (internship_id, skill_name, min_level, priority)
VALUES 
(4, 'JavaScript', 'Intermediate', 5),
(4, 'Node.js', 'Beginner', 4),
(4, 'React', 'Beginner', 3),
(4, 'MongoDB', 'Beginner', 3);

-- Insert Required Skills for Mobile App Developer (ID = 5)
INSERT INTO required_skills (internship_id, skill_name, min_level, priority)
VALUES 
(5, 'Java', 'Intermediate', 5),
(5, 'Android', 'Beginner', 4),
(5, 'REST APIs', 'Beginner', 3);

-- Insert Eligibility Criteria
INSERT INTO criteria (internship_id, min_cgpa, eligible_branches, min_year)
VALUES 
(1, 7.0, 'Computer Science,Information Technology,Electronics', 2),
(2, 6.5, 'Computer Science,Information Technology', 2),
(3, 7.5, 'Computer Science,Information Technology,Mathematics', 3),
(4, 7.0, 'Computer Science,Information Technology', 2),
(5, 7.0, 'Computer Science,Information Technology,Electronics', 2);
