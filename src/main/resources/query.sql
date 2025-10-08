CREATE DATABASE schedulEase;

use schedulEase;

CREATE TABLE user_account (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  email VARCHAR(255) NOT NULL UNIQUE,
  password_hash VARCHAR(255) NOT NULL,
  full_name VARCHAR(150) NOT NULL,
  phone VARCHAR(30),
  status ENUM('PENDING','ACTIVE','SUSPENDED') NOT NULL DEFAULT 'PENDING',
  created_at DATETIME NOT NULL,
  approved_at DATETIME NULL,
  version INT NOT NULL DEFAULT 0
);

CREATE TABLE role (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  name ENUM('ADMIN','STAFF','USER') NOT NULL UNIQUE
);

CREATE TABLE user_role (
  user_id BIGINT NOT NULL,
  role_id BIGINT NOT NULL,
  PRIMARY KEY (user_id, role_id),
  FOREIGN KEY (user_id) REFERENCES user_account(id),
  FOREIGN KEY (role_id) REFERENCES role(id)
);

CREATE TABLE staff_profile (
  id BIGINT PRIMARY KEY, -- FK to user_account.id
  department VARCHAR(120) NOT NULL,
  dob DATE NOT NULL,
  created_at DATETIME NOT NULL,
  version INT NOT NULL DEFAULT 0,
  FOREIGN KEY (id) REFERENCES user_account(id)
);

CREATE TABLE room (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(120) NOT NULL UNIQUE,
  floor VARCHAR(30) NOT NULL,
  occupancy INT NOT NULL,
  status ENUM('AVAILABLE','UNAVAILABLE','MAINTENANCE') NOT NULL DEFAULT 'AVAILABLE',
  created_by BIGINT NOT NULL,
  created_at DATETIME NOT NULL,
  updated_at DATETIME NULL,
  version INT NOT NULL DEFAULT 0,
  FOREIGN KEY (created_by) REFERENCES user_account(id)
);

CREATE TABLE equipment_type (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  name ENUM('LAPTOP','PROJECTOR','CHAIR') NOT NULL UNIQUE,
  description VARCHAR(255)
);

CREATE TABLE equipment_stock (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  equipment_type_id BIGINT NOT NULL UNIQUE,
  total_quantity INT NOT NULL,
  allocated_quantity INT NOT NULL DEFAULT 0,
  created_at DATETIME NOT NULL,
  updated_at DATETIME NULL,
  version INT NOT NULL DEFAULT 0,
  FOREIGN KEY (equipment_type_id) REFERENCES equipment_type(id)
);

CREATE TABLE booking (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT NOT NULL,
  status ENUM('PENDING','CONFIRMED','CANCELLED') NOT NULL DEFAULT 'PENDING',
  start_datetime DATETIME NOT NULL,
  end_datetime DATETIME NOT NULL,
  total_capacity_requested INT NOT NULL,
  booking_reason VARCHAR(255) NULL,
  created_at DATETIME NOT NULL,
  updated_at DATETIME NULL,
  cancelled_at DATETIME NULL,
  cancel_reason VARCHAR(255),
  version INT NOT NULL DEFAULT 0,
  FOREIGN KEY (user_id) REFERENCES user_account(id),
  CHECK (end_datetime > start_datetime)
);

-- ALTER TABLE booking 
-- ADD COLUMN booking_reason VARCHAR(255) NULL 
-- AFTER total_capacity_requested;

CREATE TABLE booking_room (
  booking_id BIGINT NOT NULL,
  room_id BIGINT NOT NULL,
  PRIMARY KEY (booking_id, room_id),
  FOREIGN KEY (booking_id) REFERENCES booking(id),
  FOREIGN KEY (room_id) REFERENCES room(id)
);

CREATE TABLE booking_equipment (
  booking_id BIGINT NOT NULL,
  equipment_type_id BIGINT NOT NULL,
  quantity INT NOT NULL,
  PRIMARY KEY (booking_id, equipment_type_id),
  FOREIGN KEY (booking_id) REFERENCES booking(id),
  FOREIGN KEY (equipment_type_id) REFERENCES equipment_type(id),
  CHECK (quantity >= 0)
);

