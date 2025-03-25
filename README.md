<p align="center">
   <img src="https://secret-stuffs.netlify.app/assets/icon-B0a1EbX8.png" width="200" height="200" alt="Logo">
</p>

# BugBoard

BugBoard is a collaborative platform for reporting software bugs, submitting solutions, and engaging in real-time coding sessions to debug issues efficiently. It enables developers to work together, share insights, and resolve problems faster. This React-based frontend interfaces with our Spring Boot backend to provide a seamless user experience for bug tracking and resolution.

---

## Features

- **User Authentication and Profile Management**: Secure login, signup, and personalized profiles
- **Bug Reporting System**: Submit detailed bug reports with code snippets or file attachments
- **Real-Time Collaboration**: Engage in live coding sessions to debug issues together
- **Solution Management**: Submit, review, and implement bug fixes
- **Advanced Search and Filters**: Find bugs by status, priority, or assignee
- **Code Editor Integration**: Built-in Monaco editor to run code while editing
- **Responsive Design**: Fully optimized for both desktop and mobile devices

---

## Tech Stack

- **Framework**: React 18
- **Routing**: React Router DOM v7
- **Code Editor**: Monaco Editor
- **Styling**: TailwindCSS
- **Icons**: Lucide React, React Icons
- **HTTP Client**: Axios
- **Authentication**: JWT
- **File Storage**: Firebase
- **Markdown Rendering**: React Markdown

---

## Prerequisites

- **Node.js**: Version 18 or above
- **npm/yarn/bun**: For package management

---

## Dependencies

### Core Dependencies
- **@monaco-editor/react**: "^4.6.0"
- **axios**: "^1.7.9"
- **cra-template**: "1.2.0"
- **firebase**: "^11.4.0"
- **js-beautify**: "^1.15.3"
- **jwt-decode**: "^4.0.0"
- **lucide-react**: "^0.474.0"
- **react**: "^18.3.1"
- **react-dom**: "^18.3.1"
- **react-icons**: "^5.4.0"
- **react-markdown**: "^9.0.3"
- **react-router-dom**: "^7.1.5"
- **react-scripts**: "5.0.1"
- **web-vitals**: "^4.2.4"

### Development Dependencies
- **postcss**: "^8.5.1"
- **tailwindcss**: "^3.4.17"

---

## Installation and Setup

### Using Docker (Recommended)

This frontend is part of a full-stack application that can be run with Docker.

1. Clone the repository:
   ```bash
   git clone https://git.cs.dal.ca/courses/2025-winter/csci-5308/group08 
   cd bugboard
   ```

2. Set up environment variables in a `.env` file
   
3. Build and run the project using Docker Compose:
   ```bash
   docker-compose up --build
   ```

4. Access the application at http://localhost

### Manual Setup

1. Clone the repository:
   ```bash
   git clone https://git.cs.dal.ca/courses/2025-winter/csci-5308/group08
   cd bugboard/frontend
   ```

2. Install dependencies:
   ```bash
   npm install
   # or
   yarn install
   # or
   bun install
   ```

3. Create a `.env` file with required environment variables:
   ```
   REACT_APP_API_BASE_URL=http://localhost:8082
   REACT_APP_FIREBASE_API_KEY=your_firebase_api_key
   REACT_APP_FIREBASE_AUTH_DOMAIN=your_firebase_auth_domain
   REACT_APP_FIREBASE_PROJECT_ID=your_firebase_project_id
   REACT_APP_FIREBASE_STORAGE_BUCKET=your_firebase_storage_bucket
   REACT_APP_FIREBASE_MESSAGING_SENDER_ID=your_firebase_messaging_sender_id
   REACT_APP_FIREBASE_APP_ID=your_firebase_app_id
   ```

4. Start the development server:
   ```bash
   npm start
   # or
   yarn start
   # or
   bun run start
   ```

5. Access the application at http://localhost:3000

---

## Project Structure

```
frontend/
├── public/               # Static files
├── src/
│   ├── assets/           # Images, icons, and other assets
│   ├── components/       # Reusable UI components
│   ├── contexts/         # React context providers
│   ├── hooks/            # Custom React hooks
│   ├── pages/            # Page components
│   ├── services/         # API services
│   ├── utils/            # Helper functions
│   ├── App.js            # Application entry point
│   ├── index.js          # React mount point
│   └── routes.js         # Application routes
├── .env                  # Environment variables
├── package.json          # Dependencies and scripts
└── tailwind.config.js    # Tailwind CSS configuration
```

---

## Available Scripts

- **start**: Runs the app in development mode
- **build**: Builds the app for production
- **test**: Runs the test suite
- **eject**: Ejects from Create React App

---

## Connection with Backend

This frontend application communicates with the Spring Boot backend through RESTful API for coding sessions and instant notifications. Make sure your backend is properly configured and running for full functionality.

---

## User Scenarios

### For Bug Reporters:
1. Sign up or log in to your account
2. Create a new bug report with detailed description, code snippets, and screenshots
3. Track the status of reported bugs
4. Communicate with developers working on the solution

### For Developers:
1. Browse assigned bugs or search for open issues
2. Engage in real-time debugging sessions
3. Submit solutions with code changes
4. Review and test proposed fixes from other developers

### For Project Managers:
1. Monitor bug resolution progress
2. Assign bugs to team members
3. Prioritize issues based on severity
4. Generate reports on bug trends and team performance

---

## Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

---

## Team Members

- Ritvik Wuyyuru
- Disha Patel
- Dharma Kevadiya
- Mency Christian

---

## License

This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.

---

## Acknowledgments

- Special thanks to Dalhousie University
- CSCI5403 Course Staff
- Special thanks to all contributors who have helped shape this project
- Inspired by the needs of client teams everywhere to streamline bug tracking and resolution
