-- =============================================
-- ScholarShelf Sample Data
-- All user passwords: "password123"
-- BCrypt hash generated with cost factor 10
-- =============================================

-- Insert users (only if not already present)
INSERT INTO users (full_name, email, password, role, active, phone, address, created_at)
SELECT 'Admin User', 'admin@scholarshelf.com', '$2a$10$dXJ3SW6G7P50lGmMQge76uBGQ1XDeVXmaZCbear15IsfeBRr4L3F2', 'ADMIN', true, '+1234567890', 'Dhaka, Bangladesh', NOW()
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'admin@scholarshelf.com');

INSERT INTO users (full_name, email, password, role, active, phone, address, created_at)
SELECT 'John Seller', 'seller@scholarshelf.com', '$2a$10$dXJ3SW6G7P50lGmMQge76uBGQ1XDeVXmaZCbear15IsfeBRr4L3F2', 'SELLER', true, '+1234567891', 'Chittagong, Bangladesh', NOW()
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'seller@scholarshelf.com');

INSERT INTO users (full_name, email, password, role, active, phone, address, created_at)
SELECT 'Jane Buyer', 'buyer@scholarshelf.com', '$2a$10$dXJ3SW6G7P50lGmMQge76uBGQ1XDeVXmaZCbear15IsfeBRr4L3F2', 'BUYER', true, '+1234567892', 'Sylhet, Bangladesh', NOW()
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'buyer@scholarshelf.com');

INSERT INTO users (full_name, email, password, role, active, phone, address, created_at)
SELECT 'Alice Seller', 'alice@scholarshelf.com', '$2a$10$dXJ3SW6G7P50lGmMQge76uBGQ1XDeVXmaZCbear15IsfeBRr4L3F2', 'SELLER', true, '+1234567893', 'Rajshahi, Bangladesh', NOW()
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'alice@scholarshelf.com');

-- Insert categories
INSERT INTO categories (name, description)
SELECT 'Fiction', 'Novels, short stories, and literary fiction'
WHERE NOT EXISTS (SELECT 1 FROM categories WHERE name = 'Fiction');

INSERT INTO categories (name, description)
SELECT 'Science', 'Physics, Chemistry, Biology, and general science'
WHERE NOT EXISTS (SELECT 1 FROM categories WHERE name = 'Science');

INSERT INTO categories (name, description)
SELECT 'Technology', 'Computer science, programming, and IT'
WHERE NOT EXISTS (SELECT 1 FROM categories WHERE name = 'Technology');

INSERT INTO categories (name, description)
SELECT 'History', 'World history, civilizations, and historical events'
WHERE NOT EXISTS (SELECT 1 FROM categories WHERE name = 'History');

INSERT INTO categories (name, description)
SELECT 'Mathematics', 'Algebra, Calculus, Statistics, and applied math'
WHERE NOT EXISTS (SELECT 1 FROM categories WHERE name = 'Mathematics');

INSERT INTO categories (name, description)
SELECT 'Philosophy', 'Ethics, logic, metaphysics, and philosophical thought'
WHERE NOT EXISTS (SELECT 1 FROM categories WHERE name = 'Philosophy');

-- Insert books (seller_id and category_id reference IDs from above)
INSERT INTO books (title, author, isbn, description, price, book_condition, image_url, available, seller_id, category_id, created_at)
SELECT 'Clean Code', 'Robert C. Martin', '9780132350884',
       'A handbook of agile software craftsmanship. Even bad code can function. But if code is not clean, it can bring a development organization to its knees.',
       29.99, 'LIKE_NEW', 'https://images-na.ssl-images-amazon.com/images/I/41xShlnTZTL._SX376_BO1,204,203,200_.jpg',
       true, (SELECT id FROM users WHERE email = 'seller@scholarshelf.com'), (SELECT id FROM categories WHERE name = 'Technology'), NOW()
WHERE NOT EXISTS (SELECT 1 FROM books WHERE isbn = '9780132350884');

INSERT INTO books (title, author, isbn, description, price, book_condition, image_url, available, seller_id, category_id, created_at)
SELECT 'The Pragmatic Programmer', 'David Thomas & Andrew Hunt', '9780135957059',
       'Your journey to mastery. A classic guide to software development best practices.',
       35.50, 'NEW', 'https://images-na.ssl-images-amazon.com/images/I/51cUVaBWtlL._SX380_BO1,204,203,200_.jpg',
       true, (SELECT id FROM users WHERE email = 'seller@scholarshelf.com'), (SELECT id FROM categories WHERE name = 'Technology'), NOW()
WHERE NOT EXISTS (SELECT 1 FROM books WHERE isbn = '9780135957059');

INSERT INTO books (title, author, isbn, description, price, book_condition, image_url, available, seller_id, category_id, created_at)
SELECT 'To Kill a Mockingbird', 'Harper Lee', '9780061120084',
       'A gripping, heart-wrenching, and wholly remarkable tale of coming-of-age in a South poisoned by virulent prejudice.',
       12.99, 'GOOD', 'https://images-na.ssl-images-amazon.com/images/I/51IXWZzlgSL._SX330_BO1,204,203,200_.jpg',
       true, (SELECT id FROM users WHERE email = 'alice@scholarshelf.com'), (SELECT id FROM categories WHERE name = 'Fiction'), NOW()
WHERE NOT EXISTS (SELECT 1 FROM books WHERE isbn = '9780061120084');

INSERT INTO books (title, author, isbn, description, price, book_condition, image_url, available, seller_id, category_id, created_at)
SELECT '1984', 'George Orwell', '9780451524935',
       'Among the seminal texts of the 20th century, Nineteen Eighty-Four is a rare work that grows more haunting as its dystopian visions steadily become reality.',
       9.99, 'FAIR', 'https://images-na.ssl-images-amazon.com/images/I/71kxa1-0mfL._SY466_.jpg',
       true, (SELECT id FROM users WHERE email = 'alice@scholarshelf.com'), (SELECT id FROM categories WHERE name = 'Fiction'), NOW()
WHERE NOT EXISTS (SELECT 1 FROM books WHERE isbn = '9780451524935');

INSERT INTO books (title, author, isbn, description, price, book_condition, image_url, available, seller_id, category_id, created_at)
SELECT 'A Brief History of Time', 'Stephen Hawking', '9780553380163',
       'A landmark volume in science writing by one of the great minds of our time.',
       15.00, 'LIKE_NEW', 'https://images-na.ssl-images-amazon.com/images/I/A1xkFZX5k-L._SY466_.jpg',
       true, (SELECT id FROM users WHERE email = 'seller@scholarshelf.com'), (SELECT id FROM categories WHERE name = 'Science'), NOW()
WHERE NOT EXISTS (SELECT 1 FROM books WHERE isbn = '9780553380163');

INSERT INTO books (title, author, isbn, description, price, book_condition, image_url, available, seller_id, category_id, created_at)
SELECT 'Sapiens: A Brief History of Humankind', 'Yuval Noah Harari', '9780062316097',
       'A groundbreaking narrative of humanitys creation and evolution that explores the ways in which biology and history have defined us.',
       18.99, 'NEW', 'https://images-na.ssl-images-amazon.com/images/I/71N3-2sYDRL._SY466_.jpg',
       true, (SELECT id FROM users WHERE email = 'alice@scholarshelf.com'), (SELECT id FROM categories WHERE name = 'History'), NOW()
WHERE NOT EXISTS (SELECT 1 FROM books WHERE isbn = '9780062316097');

INSERT INTO books (title, author, isbn, description, price, book_condition, image_url, available, seller_id, category_id, created_at)
SELECT 'Design Patterns', 'Gang of Four', '9780201633610',
       'Elements of Reusable Object-Oriented Software. The classic reference on design patterns in software engineering.',
       42.00, 'GOOD', 'https://images-na.ssl-images-amazon.com/images/I/51szD9HC9pL._SX395_BO1,204,203,200_.jpg',
       true, (SELECT id FROM users WHERE email = 'seller@scholarshelf.com'), (SELECT id FROM categories WHERE name = 'Technology'), NOW()
WHERE NOT EXISTS (SELECT 1 FROM books WHERE isbn = '9780201633610');

INSERT INTO books (title, author, isbn, description, price, book_condition, image_url, available, seller_id, category_id, created_at)
SELECT 'Introduction to Algorithms', 'Thomas H. Cormen', '9780262033848',
       'The comprehensive textbook on algorithms, widely used in universities worldwide.',
       55.00, 'NEW', 'https://images-na.ssl-images-amazon.com/images/I/41T0eBxjlSL._SX440_BO1,204,203,200_.jpg',
       true, (SELECT id FROM users WHERE email = 'alice@scholarshelf.com'), (SELECT id FROM categories WHERE name = 'Mathematics'), NOW()
WHERE NOT EXISTS (SELECT 1 FROM books WHERE isbn = '9780262033848');

INSERT INTO books (title, author, isbn, description, price, book_condition, image_url, available, seller_id, category_id, created_at)
SELECT 'Meditations', 'Marcus Aurelius', '9780140449334',
       'The private reflections of the Roman Emperor Marcus Aurelius. One of the greatest works of philosophy.',
       8.50, 'FAIR', 'https://images-na.ssl-images-amazon.com/images/I/71e+RExlv-L._SY466_.jpg',
       true, (SELECT id FROM users WHERE email = 'seller@scholarshelf.com'), (SELECT id FROM categories WHERE name = 'Philosophy'), NOW()
WHERE NOT EXISTS (SELECT 1 FROM books WHERE isbn = '9780140449334');

INSERT INTO books (title, author, isbn, description, price, book_condition, image_url, available, seller_id, category_id, created_at)
SELECT 'The Great Gatsby', 'F. Scott Fitzgerald', '9780743273565',
       'The story of the mysteriously wealthy Jay Gatsby and his love for the beautiful Daisy Buchanan.',
       10.99, 'GOOD', 'https://images-na.ssl-images-amazon.com/images/I/81af+MCATTL._SY466_.jpg',
       true, (SELECT id FROM users WHERE email = 'alice@scholarshelf.com'), (SELECT id FROM categories WHERE name = 'Fiction'), NOW()
WHERE NOT EXISTS (SELECT 1 FROM books WHERE isbn = '9780743273565');

-- Insert sample reviews
INSERT INTO reviews (rating, comment, reviewer_id, book_id, created_at)
SELECT 5, 'Excellent book! A must-read for every developer.',
       (SELECT id FROM users WHERE email = 'buyer@scholarshelf.com'),
       (SELECT id FROM books WHERE isbn = '9780132350884'), NOW()
WHERE NOT EXISTS (SELECT 1 FROM reviews WHERE reviewer_id = (SELECT id FROM users WHERE email = 'buyer@scholarshelf.com') AND book_id = (SELECT id FROM books WHERE isbn = '9780132350884'));

INSERT INTO reviews (rating, comment, reviewer_id, book_id, created_at)
SELECT 4, 'Great classic novel. The condition was as described.',
       (SELECT id FROM users WHERE email = 'buyer@scholarshelf.com'),
       (SELECT id FROM books WHERE isbn = '9780061120084'), NOW()
WHERE NOT EXISTS (SELECT 1 FROM reviews WHERE reviewer_id = (SELECT id FROM users WHERE email = 'buyer@scholarshelf.com') AND book_id = (SELECT id FROM books WHERE isbn = '9780061120084'));

-- Insert sample exchange requests
INSERT INTO exchange_requests (status, message, buyer_id, book_id, created_at, updated_at)
SELECT 'PENDING', 'I would love to get this book! Is it still available?',
       (SELECT id FROM users WHERE email = 'buyer@scholarshelf.com'),
       (SELECT id FROM books WHERE isbn = '9780135957059'), NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM exchange_requests WHERE buyer_id = (SELECT id FROM users WHERE email = 'buyer@scholarshelf.com') AND book_id = (SELECT id FROM books WHERE isbn = '9780135957059'));

INSERT INTO exchange_requests (status, message, buyer_id, book_id, created_at, updated_at)
SELECT 'ACCEPTED', 'Very interested in this science book.',
       (SELECT id FROM users WHERE email = 'buyer@scholarshelf.com'),
       (SELECT id FROM books WHERE isbn = '9780553380163'), NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM exchange_requests WHERE buyer_id = (SELECT id FROM users WHERE email = 'buyer@scholarshelf.com') AND book_id = (SELECT id FROM books WHERE isbn = '9780553380163'));

-- Insert sample messages
INSERT INTO messages (content, sender_id, receiver_id, is_read, sent_at)
SELECT 'Hi! I am interested in your book "Clean Code". Is it in good condition?',
       (SELECT id FROM users WHERE email = 'buyer@scholarshelf.com'),
       (SELECT id FROM users WHERE email = 'seller@scholarshelf.com'),
       true, NOW()
WHERE NOT EXISTS (SELECT 1 FROM messages WHERE sender_id = (SELECT id FROM users WHERE email = 'buyer@scholarshelf.com') AND receiver_id = (SELECT id FROM users WHERE email = 'seller@scholarshelf.com'));

INSERT INTO messages (content, sender_id, receiver_id, is_read, sent_at)
SELECT 'Yes, it is in excellent condition! Almost like new. Would you like to proceed?',
       (SELECT id FROM users WHERE email = 'seller@scholarshelf.com'),
       (SELECT id FROM users WHERE email = 'buyer@scholarshelf.com'),
       false, NOW()
WHERE NOT EXISTS (SELECT 1 FROM messages WHERE sender_id = (SELECT id FROM users WHERE email = 'seller@scholarshelf.com') AND receiver_id = (SELECT id FROM users WHERE email = 'buyer@scholarshelf.com'));

