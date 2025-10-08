-- ============================================
-- 1. USER_ACCOUNT - Users, Staff, and Admin
-- ============================================

INSERT INTO user_account (email, password_hash, full_name, phone, status, created_at, approved_at, version) VALUES
-- Admin User
('admin@amdocs.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'System Administrator', '9999999999', 'ACTIVE', '2025-01-01 09:00:00', '2025-01-01 09:00:00', 0),

-- Staff Members
('rajesh.kumar@amdocs.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Rajesh Kumar', '9876543210', 'ACTIVE', '2025-01-05 10:00:00', '2025-01-05 10:30:00', 0),
('priya.sharma@amdocs.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Priya Sharma', '9876543211', 'ACTIVE', '2025-01-06 11:00:00', '2025-01-06 11:30:00', 0),

-- Regular Users (Active)
('divyansh.sinha@amdocs.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Divyansh Sinha', '9123456789', 'ACTIVE', '2025-02-01 14:00:00', '2025-02-01 15:00:00', 0),
('vivek.dhir@amdocs.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Vivek Dhir', '9123456790', 'ACTIVE', '2025-02-02 10:00:00', '2025-02-02 11:00:00', 0),
('divyansh.bansal@amdocs.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Divyansh Bansal', '9123456791', 'ACTIVE', '2025-02-03 09:00:00', '2025-02-03 10:00:00', 0),
('abhishek.naithani@amdocs.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Abhishek Naithani', '9123456792', 'ACTIVE', '2025-02-04 11:00:00', '2025-02-04 12:00:00', 0),

-- Pending Users (not yet approved)
('neha.gupta@amdocs.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Neha Gupta', '9123456793', 'PENDING', '2025-10-07 16:00:00', NULL, 0),
('rohit.verma@amdocs.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Rohit Verma', '9123456794', 'PENDING', '2025-10-08 10:00:00', NULL, 0),

-- Suspended User
('suspended.user@amdocs.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Suspended User', '9123456795', 'SUSPENDED', '2025-01-15 12:00:00', '2025-01-15 13:00:00', 0);

-- ============================================
-- 2. ROLE - Define roles
-- ============================================

INSERT INTO role (name) VALUES
('ADMIN'),
('STAFF'),
('USER');

-- ============================================
-- 3. USER_ROLE - Assign roles to users
-- ============================================

INSERT INTO user_role (user_id, role_id) VALUES
-- Admin
(1, 1),  -- admin@amdocs.com has ADMIN role

-- Staff
(2, 2),  -- rajesh.kumar@amdocs.com has STAFF role
(3, 2),  -- priya.sharma@amdocs.com has STAFF role

-- Regular Users
(4, 3),  -- divyansh.sinha@amdocs.com has USER role
(5, 3),  -- vivek.dhir@amdocs.com has USER role
(6, 3),  -- divyansh.bansal@amdocs.com has USER role
(7, 3),  -- abhishek.naithani@amdocs.com has USER role
(8, 3),  -- neha.gupta@amdocs.com has USER role
(9, 3),  -- rohit.verma@amdocs.com has USER role
(10, 3); -- suspended.user@amdocs.com has USER role

-- ============================================
-- 4. STAFF_PROFILE - Staff member details
-- ============================================

INSERT INTO staff_profile (id, department, dob, created_at, version) VALUES
(2, 'IT Operations', '1990-05-15', '2025-01-05 10:30:00', 0),
(3, 'Facilities Management', '1992-08-22', '2025-01-06 11:30:00', 0);

-- ============================================
-- 5. ROOM - Meeting rooms and spaces
-- ============================================

INSERT INTO room (name, floor, occupancy, status, created_by, created_at, updated_at, version) VALUES
('Conference Room A', '1st Floor', 20, 'AVAILABLE', 2, '2025-01-10 09:00:00', NULL, 0),
('Conference Room B', '1st Floor', 15, 'AVAILABLE', 2, '2025-01-10 09:30:00', NULL, 0),
('Meeting Room 3A', '3rd Floor', 8, 'AVAILABLE', 2, '2025-01-10 10:00:00', NULL, 0),
('Meeting Room 3B', '3rd Floor', 8, 'AVAILABLE', 2, '2025-01-10 10:30:00', NULL, 0),
('Meeting Room 4A', '4th Floor', 10, 'AVAILABLE', 3, '2025-01-11 09:00:00', NULL, 0),
('Meeting Room 4B', '4th Floor', 10, 'AVAILABLE', 3, '2025-01-11 09:30:00', NULL, 0),
('Board Room', '5th Floor', 30, 'AVAILABLE', 3, '2025-01-11 10:00:00', NULL, 0),
('Training Room 1', '2nd Floor', 25, 'AVAILABLE', 2, '2025-01-12 09:00:00', NULL, 0),
('Training Room 2', '2nd Floor', 25, 'UNAVAILABLE', 2, '2025-01-12 09:30:00', '2025-10-07 14:00:00', 0),
('Huddle Space 6A', '6th Floor', 4, 'MAINTENANCE', 3, '2025-01-12 10:00:00', '2025-10-06 16:00:00', 0);

-- ============================================
-- 6. EQUIPMENT_TYPE - Types of equipment
-- ============================================

INSERT INTO equipment_type (name, description) VALUES
('LAPTOP', 'Dell Latitude business laptops with Windows 11'),
('PROJECTOR', 'Epson EB-X51 3LCD projectors with HDMI'),
('CHAIR', 'Ergonomic office chairs for extended meetings');

-- ============================================
-- 7. EQUIPMENT_STOCK - Available equipment
-- ============================================

INSERT INTO equipment_stock (equipment_type_id, total_quantity, allocated_quantity, created_at, updated_at, version) VALUES
(1, 50, 12, '2025-01-10 09:00:00', '2025-10-08 09:00:00', 3),  -- 50 Laptops, 12 allocated, 38 available
(2, 20, 5, '2025-01-10 09:30:00', '2025-10-08 09:00:00', 2),   -- 20 Projectors, 5 allocated, 15 available
(3, 100, 30, '2025-01-10 10:00:00', '2025-10-08 09:00:00', 1); -- 100 Chairs, 30 allocated, 70 available

-- ============================================
-- 8. BOOKING - Room and equipment bookings
-- ============================================

INSERT INTO booking (user_id, status, start_datetime, end_datetime, total_capacity_requested, booking_reason, created_at, updated_at, cancelled_at, cancel_reason, version) VALUES
-- Confirmed upcoming bookings
(4, 'CONFIRMED', '2025-10-09 10:00:00', '2025-10-09 12:00:00', 15, 'Sprint Planning Meeting', '2025-10-05 14:00:00', '2025-10-05 15:00:00', NULL, NULL, 1),
(5, 'CONFIRMED', '2025-10-09 14:00:00', '2025-10-09 16:00:00', 8, 'Technical Design Discussion', '2025-10-06 09:00:00', '2025-10-06 10:00:00', NULL, NULL, 1),
(6, 'CONFIRMED', '2025-10-10 09:00:00', '2025-10-10 11:00:00', 20, 'Client Presentation', '2025-10-07 11:00:00', '2025-10-07 12:00:00', NULL, NULL, 1),
(7, 'CONFIRMED', '2025-10-10 15:00:00', '2025-10-10 17:00:00', 6, 'Team Brainstorming Session', '2025-10-08 10:00:00', '2025-10-08 10:30:00', NULL, NULL, 0),

-- Pending bookings (awaiting approval/confirmation)
(4, 'PENDING', '2025-10-11 10:00:00', '2025-10-11 12:00:00', 10, 'Product Demo', '2025-10-08 11:00:00', NULL, NULL, NULL, 0),
(5, 'PENDING', '2025-10-12 14:00:00', '2025-10-12 16:00:00', 25, 'Training Session', '2025-10-08 11:15:00', NULL, NULL, NULL, 0),

-- Past completed bookings
(4, 'CONFIRMED', '2025-10-05 10:00:00', '2025-10-05 12:00:00', 8, 'Daily Standup', '2025-10-04 16:00:00', '2025-10-04 17:00:00', NULL, NULL, 1),
(5, 'CONFIRMED', '2025-10-06 14:00:00', '2025-10-06 16:00:00', 10, 'Code Review Session', '2025-10-05 09:00:00', '2025-10-05 10:00:00', NULL, NULL, 1),
(6, 'CONFIRMED', '2025-10-07 09:00:00', '2025-10-07 11:00:00', 15, 'Architecture Review', '2025-10-06 14:00:00', '2025-10-06 15:00:00', NULL, NULL, 1),

-- Cancelled bookings
(7, 'CANCELLED', '2025-10-09 09:00:00', '2025-10-09 11:00:00', 12, 'Team Meeting', '2025-10-05 10:00:00', '2025-10-07 16:00:00', '2025-10-07 16:00:00', 'Meeting postponed due to client conflict', 2),
(4, 'CANCELLED', '2025-10-08 14:00:00', '2025-10-08 16:00:00', 5, 'Interview Panel', '2025-10-06 11:00:00', '2025-10-07 09:00:00', '2025-10-07 09:00:00', 'Candidate cancelled', 1);

-- ============================================
-- 9. BOOKING_ROOM - Room allocations
-- ============================================

INSERT INTO booking_room (booking_id, room_id) VALUES
-- Booking 1: Sprint Planning
(1, 2),  -- Conference Room B

-- Booking 2: Technical Design
(2, 3),  -- Meeting Room 3A

-- Booking 3: Client Presentation
(3, 1),  -- Conference Room A

-- Booking 4: Brainstorming
(4, 4),  -- Meeting Room 3B

-- Booking 5: Product Demo (Pending)
(5, 5),  -- Meeting Room 4A

-- Booking 6: Training (Pending)
(6, 8),  -- Training Room 1

-- Past bookings
(7, 3),  -- Meeting Room 3A
(8, 5),  -- Meeting Room 4A
(9, 2),  -- Conference Room B

-- Cancelled bookings
(10, 6), -- Meeting Room 4B
(11, 4); -- Meeting Room 3B

-- ============================================
-- 10. BOOKING_EQUIPMENT - Equipment allocations
-- ============================================

INSERT INTO booking_equipment (booking_id, equipment_type_id, quantity) VALUES
-- Booking 1: Sprint Planning (2 laptops, 1 projector)
(1, 1, 2),
(1, 2, 1),

-- Booking 2: Technical Design (3 laptops, 1 projector)
(2, 1, 3),
(2, 2, 1),

-- Booking 3: Client Presentation (1 laptop, 2 projectors, 20 chairs)
(3, 1, 1),
(3, 2, 2),
(3, 3, 20),

-- Booking 4: Brainstorming (2 laptops)
(4, 1, 2),

-- Booking 5: Product Demo (1 laptop, 1 projector)
(5, 1, 1),
(5, 2, 1),

-- Booking 6: Training (3 laptops, 1 projector, 10 chairs)
(6, 1, 3),
(6, 2, 1),
(6, 3, 10),

-- Past bookings
(7, 1, 2),  -- 2 laptops
(8, 1, 1),  -- 1 laptop
(8, 2, 1),  -- 1 projector
(9, 1, 2),  -- 2 laptops
(9, 2, 1);  -- 1 projector

-- Note: Cancelled bookings (10, 11) don't need equipment entries since they were cancelled
