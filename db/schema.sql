IF OBJECT_ID('dbo.users', 'U') IS NULL
BEGIN
    CREATE TABLE dbo.users (
        id INT IDENTITY(1,1) PRIMARY KEY,
        username NVARCHAR(50) NOT NULL UNIQUE,
        email NVARCHAR(255) NOT NULL UNIQUE,
        password_hash NVARCHAR(512) NOT NULL,
        created_at DATETIME2 NOT NULL DEFAULT SYSUTCDATETIME(),
        skills NVARCHAR(2000),
        experience NVARCHAR(4000),
        profile_picture VARBINARY(MAX),
        role NVARCHAR(20) NOT NULL DEFAULT 'client'
    );
END
ELSE
BEGIN
    IF NOT EXISTS (SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME='users' AND COLUMN_NAME='skills')
    BEGIN
        ALTER TABLE dbo.users ADD skills NVARCHAR(2000);
    END;
    IF NOT EXISTS (SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME='users' AND COLUMN_NAME='experience')
    BEGIN
        ALTER TABLE dbo.users ADD experience NVARCHAR(4000);
    END;
    IF NOT EXISTS (SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME='users' AND COLUMN_NAME='profile_picture')
    BEGIN
        ALTER TABLE dbo.users ADD profile_picture VARBINARY(MAX);
    END;
    IF NOT EXISTS (SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME='users' AND COLUMN_NAME='role')
    BEGIN
        ALTER TABLE dbo.users ADD role NVARCHAR(20) NOT NULL DEFAULT 'client';
    END;
END;

GO

IF OBJECT_ID('dbo.JOBS', 'U') IS NULL
BEGIN
    CREATE TABLE dbo.JOBS (
        id INT IDENTITY(1,1) PRIMARY KEY,
        title NVARCHAR(200) NOT NULL,
        description NVARCHAR(4000),
        budget DECIMAL(10, 2),
        deadline DATE,
        client_id INT NOT NULL,
        posted_at DATETIME2 NOT NULL DEFAULT SYSUTCDATETIME(),
        CONSTRAINT FK_JOBS_client FOREIGN KEY (client_id) REFERENCES dbo.users(id)
    );
END;

GO

IF OBJECT_ID('dbo.APPLICATIONS', 'U') IS NULL
BEGIN
    CREATE TABLE dbo.APPLICATIONS (
        id INT IDENTITY(1,1) PRIMARY KEY,
        job_id INT NOT NULL,
        freelancer_id INT NOT NULL,
        cover_letter NVARCHAR(4000),
        status NVARCHAR(20) NOT NULL DEFAULT 'pending',
        applied_at DATETIME2 NOT NULL DEFAULT SYSUTCDATETIME(),
        CONSTRAINT FK_APPS_job FOREIGN KEY (job_id) REFERENCES dbo.JOBS(id),
        CONSTRAINT FK_APPS_freelancer FOREIGN KEY (freelancer_id) REFERENCES dbo.users(id),
        CONSTRAINT UQ_application UNIQUE (job_id, freelancer_id)
    );
END;

GO

IF OBJECT_ID('dbo.MESSAGES', 'U') IS NULL
BEGIN
    CREATE TABLE dbo.MESSAGES (
        id INT IDENTITY(1,1) PRIMARY KEY,
        sender_id INT NOT NULL,
        receiver_id INT NOT NULL,
        content NVARCHAR(2000) NOT NULL,
        sent_at DATETIME2 NOT NULL DEFAULT SYSUTCDATETIME(),
        is_read BIT NOT NULL DEFAULT 0,
        CONSTRAINT FK_MSG_sender   FOREIGN KEY (sender_id)   REFERENCES dbo.users(id),
        CONSTRAINT FK_MSG_receiver FOREIGN KEY (receiver_id) REFERENCES dbo.users(id)
    );
END;

