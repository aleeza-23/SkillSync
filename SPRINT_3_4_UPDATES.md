# SkillSync Sprint 3.1-4.2: Profile Management & Logout

## Summary

Successfully implemented user profile management and logout functionality across all 6 tasks. Users can now:
- View and edit their skills and experience
- Upload and manage profile pictures
- See their complete profile on a dedicated dashboard
- Log out securely with session cleanup

---

## Tasks Completed

### ✅ Task 3.1 – Create profile form UI (skills, experience, profile pic)
- Created `profile.fxml` with a professional form layout
- TextArea fields for skills and experience (multiline input)
- ImageView for profile picture preview
- FileChooser button for image upload
- Back button to return to dashboard

### ✅ Task 3.2 – Implement skills and experience storage
- Extended database schema with `skills` (NVARCHAR(2000)) and `experience` (NVARCHAR(4000)) columns
- Updated `UserRepository.updateUserProfile()` to persist text data
- ProfileController saves changes to database and updates UserSession

### ✅ Task 3.3 – Implement profile picture upload
- FileChooser restricts selection to .jpg, .jpeg, .png files
- Image file converted to byte[] and stored in `profile_picture` (VARBINARY(MAX))
- Preview updated immediately after upload
- Handles cases where no picture is selected (NULL in database)

### ✅ Task 3.4 – Display profile information in user dashboard
- Created `dashboard.fxml` with clean profile display
- Shows username, email, skills, experience, and profile picture
- ImageView renders byte[] pictures using ByteArrayInputStream
- "Edit Profile" button navigates to profile form
- Default messages when fields are empty

### ✅ Task 4.1 – Implement logout button
- Logout button in dashboard top bar
- Calls `UserSession.logout()` to clear session data
- Destroys current User object reference

### ✅ Task 4.2 – Redirect to login page after logout
- After logout, user is redirected to login.fxml
- Scene switch uses standard stage/FXML pattern (consistent with signup flow)
- Stage title updated to "SkillSync - Log In"

---

## Files Created

### New Java Classes
1. **User.java** – POJO with getters/setters for:
   - id, username, email, passwordHash (existing fields)
   - skills, experience, profilePicture (new fields)

2. **UserSession.java** – Static session helper for tracking logged-in user:
   - `setCurrentUser(User)` / `getCurrentUser()`
   - `setCurrentUserId(int)` / `getCurrentUserId()`
   - `isLoggedIn()` boolean check
   - `logout()` clears all session state

3. **DashboardController.java** – Displays user profile:
   - `initialize()` loads current user from UserSession and populates UI
   - `handleEditProfile()` navigates to profile editing form
   - `handleLogout()` clears session and returns to login

4. **ProfileController.java** – Handles profile editing:
   - `initialize()` loads current user's data into form fields
   - `handleUploadPicture()` uses FileChooser, converts image to byte[]
   - `handleSave()` persists changes to database and UserSession
   - `handleBack()` returns to dashboard

### New UI Files
1. **dashboard.fxml** – Dashboard layout:
   - Header with app title and logout button
   - Left side: Profile picture with border
   - Right side: User info (username, email) + skills/experience display
   - Edit Profile button to open profile editing form

2. **profile.fxml** – Profile editing form:
   - Left side: Picture preview + upload button
   - Right side: Skills and Experience TextArea fields
   - Username/Email display (read-only)
   - Save Changes button
   - Back to Dashboard button

---

## Files Modified

### 1. **db/schema.sql**
```sql
-- Added 3 new columns to dbo.users:
ALTER TABLE dbo.users ADD skills NVARCHAR(2000);
ALTER TABLE dbo.users ADD experience NVARCHAR(4000);
ALTER TABLE dbo.users ADD profile_picture VARBINARY(MAX);
```
- Script now includes CREATE IF NOT EXISTS and ALTER IF NOT EXISTS checks
- Safely handles both fresh installations and upgrades

### 2. **DatabaseManager.java**
- Changed from hardcoded SQL string to reading `db/schema.sql` file
- `initializeSchema()` now executes full schema script on startup
- Supports all ALTER TABLE statements for existing databases

### 3. **UserRepository.java**
- **New Method:** `getUserIdByIdentifier(String)` – Returns user ID for login flow
- **New Method:** `getUserById(int)` – Fetches complete User object with all fields
- **New Method:** `updateUserProfile(int userId, String skills, String experience, byte[] picture)` – Updates profile fields
- **New Helper:** `mapResultSetToUser()` – Converts ResultSet to User POJO

### 4. **LoginController.java**
- After successful login:
  - Fetches complete User object from database
  - Sets UserSession with the User object
  - **Navigates to dashboard** (instead of just showing success message)
- New private method `navigateToDashboard()` for scene switching
- Added UserRepository dependency for fetching user data

### 5. **SignupController.java**
- `handleNavigateToLogin()` now calls `UserSession.logout()` before switching scenes
- Ensures clean session state when moving between signup and login

---

## Architecture & Design Patterns

### Session Management
- **UserSession** is a static helper that holds a reference to the current User POJO
- All controllers can check `UserSession.isLoggedIn()` and access user data
- Session is cleared on logout
- Eliminates need for database queries to fetch user data on every UI update

### CRUD Operations
- **Create:** AuthService.signup() → UserRepository.createUser()
- **Read:** UserRepository.getUserById() / getUserIdByIdentifier()
- **Update:** ProfileController.handleSave() → UserRepository.updateUserProfile()
- **Delete:** UserSession.logout() clears reference (no hard delete)

### UI Navigation Pattern
All controllers follow the same pattern for scene switching:
```java
FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("screen.fxml"));
Scene scene = new Scene(fxmlLoader.load());
Stage stage = (Stage) button.getScene().getWindow();
stage.setScene(scene);
stage.setTitle("Title");
```

### Image Handling
- **Upload:** `File → Files.readAllBytes() → byte[]`
- **Storage:** `byte[] → VARBINARY(MAX) SQL column`
- **Display:** `byte[] → ByteArrayInputStream → Image → ImageView`

---

## Database Schema Changes

### New Columns in dbo.users
| Column | Type | Nullable | Notes |
|--------|------|----------|-------|
| skills | NVARCHAR(2000) | YES | User's skill list (comma-separated or formatted) |
| experience | NVARCHAR(4000) | YES | User's work experience or biography |
| profile_picture | VARBINARY(MAX) | YES | Binary image data (JPG/PNG) |

### Migration
- **Fresh Install:** db/schema.sql creates table with all columns
- **Existing Database:** ALTER TABLE statements add missing columns (idempotent)
- No data loss; nullable columns default to NULL

---

## User Flow

### Login → Dashboard → Profile → Save → Dashboard → Logout

1. **User logs in** on login.fxml
   - LoginController validates credentials
   - Fetches User from database
   - Sets UserSession
   - Navigates to dashboard.fxml

2. **Dashboard displays profile**
   - DashboardController.initialize() loads from UserSession
   - Shows username, email, skills, experience, picture
   - User can click "Edit Profile"

3. **User edits profile** on profile.fxml
   - ProfileController.initialize() pre-fills form from UserSession
   - User can upload new picture (FileChooser)
   - Clicks "Save Changes"

4. **Save to database and session**
   - ProfileController calls UserRepository.updateUserProfile()
   - Updates UserSession with new User object
   - Success message displayed

5. **Return to dashboard**
   - User clicks "Back to Dashboard"
   - DashboardController refreshes with updated profile

6. **Logout**
   - User clicks "Logout"
   - DashboardController calls UserSession.logout()
   - Clears session and navigates to login.fxml

---

## Setup Instructions

### Prerequisites
- Java 11+ with JavaFX 21+ SDK
- Azure SQL Server instance with connectivity
- Git repository with all files

### Configuration

1. **Set Database Connection Environment Variables**
   ```bash
   # Option A: Full JDBC URL
   export SKILLSYNC_DB_URL="jdbc:sqlserver://skillsyncserver.database.windows.net:1433;database=skillsync;user=skillsync12@skillsyncserver;password=YOUR_PASSWORD;encrypt=true;trustServerCertificate=false;hostNameInCertificate=*.database.windows.net;loginTimeout=30;"

   # Option B: Just password (uses default server config)
   export PASSWORD="YOUR_PASSWORD"
   ```

2. **Restart the Application**
   - DatabaseManager.initializeSchema() runs on startup
   - Schema script (db/schema.sql) is executed
   - New columns are automatically added/created
   - No manual SQL script execution needed

3. **First Run**
   - App connects to database
   - Schema initialization completes
   - Login screen appears
   - You can now signup and test the flow

### Testing the New Features

**Signup & Login:**
1. Click "Sign Up" on login screen
2. Create new account (username, email, password)
3. Login with credentials
4. Dashboard loads with empty profile

**Edit Profile:**
1. Click "Edit Profile" on dashboard
2. Fill in "Skills" (e.g., "Java, Python, SQL")
3. Fill in "Experience" (e.g., "5 years software development")
4. Click "Upload Picture" → Select JPG/PNG file
5. Click "Save Changes"
6. See success message

**View Updated Profile:**
1. Click "Back to Dashboard"
2. Verify skills, experience, and picture are displayed
3. Re-edit to change profile picture again

**Logout:**
1. Click "Logout" button in dashboard header
2. Redirected to login screen
3. Session is cleared
4. Can login with different account

---

## Code Quality & Consistency

✅ **Architecture:** Follows existing MVC pattern (Controllers + FXML + Service/Repository layers)  
✅ **Exception Handling:** SQLException wrapped with meaningful messages, stack traces logged  
✅ **Naming:** Matches existing conventions (handleXxx, javadoc comments where needed)  
✅ **Security:** Prepared statements, no SQL injection, password hashing unchanged  
✅ **Comments:** Minimal, code is self-documenting (same style as existing files)  
✅ **Dependencies:** Only standard Java/JavaFX (no external libraries added)  

---

## Rollback Notes (if needed)

To revert to previous state:
1. Delete: `User.java`, `UserSession.java`, `DashboardController.java`, `ProfileController.java`, `dashboard.fxml`, `profile.fxml`
2. Restore: `DatabaseManager.java`, `UserRepository.java`, `LoginController.java`, `SignupController.java`, `db/schema.sql` to original versions
3. The database columns (skills, experience, profile_picture) can remain or be dropped with: `ALTER TABLE dbo.users DROP COLUMN skills, experience, profile_picture;`

---

## Summary of Implementation

| Component | Type | Status | Notes |
|-----------|------|--------|-------|
| User POJO | Java Class | ✅ Complete | All getters/setters for 7 fields |
| UserSession | Java Class | ✅ Complete | Static session management |
| DashboardController | Java Class | ✅ Complete | Displays profile + logout |
| ProfileController | Java Class | ✅ Complete | Edit profile + image upload |
| Dashboard UI | FXML | ✅ Complete | Professional layout |
| Profile Edit UI | FXML | ✅ Complete | Form fields + file picker |
| Database Schema | SQL | ✅ Complete | 3 new columns, idempotent |
| DatabaseManager | Java Class | ✅ Updated | Reads schema from file |
| UserRepository | Java Class | ✅ Updated | +3 new methods for CRUD |
| LoginController | Java Class | ✅ Updated | Sets session + navigates |
| SignupController | Java Class | ✅ Updated | Clears session on nav |

---

## Next Steps (Future Sprints)

- **Password Reset:** Add forgot password flow
- **Profile Picture Validation:** Image size/format validation before upload
- **Skill Endorsements:** Allow users to endorse each other's skills
- **Search Users:** Find and view other user profiles
- **Settings:** User preferences, privacy controls
- **Activity Log:** Track profile edits, logins, etc.

---

**End of Sprint 3.1-4.2 Implementation**
