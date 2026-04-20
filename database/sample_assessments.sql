-- Sample Data for Skill Assessment Service

USE skill_assessment_db;

-- Insert Sample Assessments
INSERT INTO assessments (title, skill_category, total_marks, duration, created_at)
VALUES 
('Java Fundamentals Test', 'Java', 100, 60, NOW()),
('JavaScript Basics', 'JavaScript', 100, 45, NOW()),
('Python Programming', 'Python', 100, 60, NOW()),
('React Framework Assessment', 'React', 100, 50, NOW()),
('Data Structures & Algorithms', 'DSA', 100, 90, NOW()),
('Spring Boot Practical', 'Spring Boot', 100, 75, NOW()),
('Machine Learning Basics', 'Machine Learning', 100, 60, NOW());
