# 🚀 LinkUp AI

## AI-Powered LinkedIn Messaging Assistant

LinkUp AI is a full-stack AI productivity platform that helps users generate personalized LinkedIn messages for referrals, recruiter outreach, connection requests, follow-ups, and conversations.

The platform combines a **React frontend**, **Spring Boot backend**, **Groq-powered LLM integration**, and a **Chrome Extension** to bring AI-assisted professional communication directly into the LinkedIn workflow.

The project is containerized with **Docker and Docker Compose** and includes a **GitHub Actions CI pipeline** for automated backend, frontend, and Docker build validation.

---

# 🎥 Demo Video

<p align="center">
  <a href="https://drive.google.com/file/d/1AX5lyAVrnhNZ1xDoS_78twR2Jn-2aCS3/view?usp=sharing">
    <img src="Screenshots/Screenshot 2026-06-03 005319.png" width="85%" alt="LinkUp AI Demo"/>
  </a>
</p>

<p align="center">
🎬 Click the image above to watch the complete project demonstration
</p>

---

# 📸 Project Screenshots

## 🏠 Dashboard

<p align="center">
  <img src="Screenshots/Screenshot 2026-06-03 005319.png" width="90%" alt="Dashboard"/>
</p>

---

## 🤝 Referral Request Generator

<p align="center">
  <img src="Screenshots/Screenshot 2026-06-03 005609.png" width="90%" alt="Referral Request"/>
</p>

---

## 💼 Recruiter Outreach Generator

<p align="center">
  <img src="Screenshots/Screenshot 2026-06-03 005700.png" width="90%" alt="Recruiter Outreach"/>
</p>

---

## 💬 LinkedIn Reply Assistant

<p align="center">
  <img src="Screenshots/Screenshot 2026-06-03 005748.png" width="90%" alt="LinkedIn Reply"/>
</p>

---

## 🧩 Chrome Extension

<p align="center">
  <img src="Screenshots/Screenshot 2026-06-03 224622.png" width="90%" alt="Chrome Extension"/>
</p>

---

# ✨ Features

- 🤝 AI-powered referral request generation
- 💼 Recruiter outreach message generation
- 🔗 LinkedIn connection request generation
- 💬 Context-aware LinkedIn reply generation
- ⚡ Fast reply mode for the Chrome Extension
- 🎭 Multiple communication tones
- 🧠 Action-specific prompt engineering
- 📋 One-click message copying
- 🌙 Dark mode
- 🧩 Chrome Extension integration
- 🔍 LinkedIn content extraction
- 🐳 Dockerized backend and frontend
- 🔗 Docker Compose full-stack setup
- 🚀 GitHub Actions CI
- 🔐 Environment-based API key configuration

---

# 🏗️ Architecture

```text
                         LinkedIn
                            │
                            ▼
                   Chrome Extension
                            │
                            ▼
                     React Frontend
                            │
                            ▼
                    Nginx Container
                            │
                            ▼
                  Spring Boot Backend
                            │
                            ▼
                        Groq API
                            │
                            ▼
                    AI Generated Reply
```

---

# 🛠️ Tech Stack

| Layer | Technology |
|---|---|
| Frontend | React |
| UI | Material UI |
| Build Tool | Vite |
| HTTP Client | Axios |
| Backend | Spring Boot 3.3 |
| Language | Java 21 |
| API Client | Spring WebClient |
| AI Provider | Groq |
| LLM | Llama |
| Extension | JavaScript |
| Validation | Jakarta Validation |
| Serialization | Jackson |
| Build Tool | Maven |
| Web Server | Nginx |
| Containerization | Docker |
| Orchestration | Docker Compose |
| CI/CD | GitHub Actions |

---

# ⚙️ Core Workflows

## 🤝 Referral Requests

Generate personalized referral messages using:

- Employee name
- Company
- Target role
- Job description
- Selected tone

---

## 💼 Recruiter Outreach

Generate concise recruiter messages designed to communicate:

- Relevant skills
- Interest in the opportunity
- Professional intent
- Role-specific context

---

## 🔗 Connection Requests

Generate natural LinkedIn connection messages while keeping the message concise and professional.

---

## 💬 LinkedIn Replies

Paste an existing LinkedIn conversation and generate an appropriate response based on the conversation context.

---

## ⚡ Fast Reply Mode

The Chrome Extension uses a dedicated fast-reply endpoint optimized for quick AI responses.

```text
POST /api/linkedin/fast-reply
```

---

# 🧠 AI Processing

LinkUp AI uses action-specific prompt engineering rather than treating every request as a generic chatbot query.

The system maps user actions to specialized generation workflows.

```text
Referral Request
        ↓
REFERRAL_REQUEST

Cold Pitch
        ↓
COLD_PITCH

LinkedIn Reply
        ↓
LINKEDIN_REPLY

Connection Request
        ↓
CONNECTION_REQUEST
```

The backend then constructs an appropriate prompt based on:

- Action
- Tone
- Recipient
- Company
- Target role
- Message context

---

# 🧩 Chrome Extension

The LinkUp AI Chrome Extension integrates directly with LinkedIn.

### Capabilities

- LinkedIn page content detection
- Dynamic UI injection
- Context extraction
- AI reply generation
- Fast reply workflow
- Message assistance without leaving LinkedIn

---

# 🏗️ Project Structure

```text
LinkUp/
│
├── linkup/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/
│   │   │   └── resources/
│   │   ├── test/
│   │   └── Dockerfile
│   │
│   └── pom.xml
│
├── linkup-react/
│   ├── src/
│   ├── public/
│   ├── Dockerfile
│   ├── nginx.conf
│   ├── package.json
│   └── package-lock.json
│
├── linkup-ext/
│   └── Chrome Extension files
│
├── .github/
│   └── workflows/
│       └── ci.yml
│
├── docker-compose.yml
├── README.md
└── LICENSE
```

---

# 🐳 Docker

The complete application can be run using Docker Compose.

### Services

```text
Frontend
React + Nginx
     │
     ▼
Backend
Spring Boot
     │
     ▼
Groq API
```

### Run the application

Clone the repository:

```bash
git clone https://github.com/prijithjohn/linkup.git

cd linkup
```

Create a `.env` file:

```env
GROQ_API_KEY=your_groq_api_key
```

Then run:

```bash
docker compose up --build
```

### Application

Frontend:

```text
http://localhost:4173
```

Backend:

```text
http://localhost:8090
```

Stop the containers:

```bash
docker compose down
```

---

# 🔑 Environment Variables

LinkUp AI keeps API credentials outside the source code.

### Required

```env
GROQ_API_KEY=your_groq_api_key
```

The backend reads the key using:

```properties
groq.api.key=${GROQ_API_KEY}
```

Never commit the `.env` file or real API credentials to Git.

---

# 🔄 Local Development

## Backend

Navigate to the backend:

```bash
cd linkup
```

Set the Groq API key.

PowerShell:

```powershell
$env:GROQ_API_KEY="your_groq_api_key"
```

Run:

```bash
mvn spring-boot:run
```

Backend:

```text
http://localhost:8086
```

---

## Frontend

Navigate to:

```bash
cd linkup-react
```

Install dependencies:

```bash
npm install
```

Run development server:

```bash
npm run dev
```

---

# 🧩 Chrome Extension Setup

1. Open Chrome.

2. Navigate to:

```text
chrome://extensions
```

3. Enable **Developer Mode**.

4. Select **Load unpacked**.

5. Select:

```text
linkup-ext/
```

6. Open LinkedIn.

7. Use the LinkUp AI assistant.

---

# 🧪 Testing & Validation

The project includes automated build validation through GitHub Actions.

### Backend

```bash
mvn test
```

### Frontend

```bash
npm ci
npm run build
```

### Docker

```bash
docker build -t linkup-backend ./linkup

docker build -t linkup-frontend ./linkup-react
```

### Full Stack

```bash
docker compose up --build
```

---

# 🚀 CI/CD

GitHub Actions automatically validates the project.

```text
Git Push
   │
   ▼
GitHub Actions
   │
   ├── Java 21
   │
   ├── Maven Build & Tests
   │
   ├── Node.js 20
   │
   ├── React Build
   │
   ├── Backend Docker Build
   │
   └── Frontend Docker Build
```

The CI pipeline helps detect:

- Backend compilation failures
- Test failures
- Frontend build failures
- Docker build failures

---

# 🔐 Security

LinkUp AI uses environment-based configuration for sensitive credentials.

Implemented practices include:

- API keys stored outside source code
- Environment variable configuration
- No frontend exposure of Groq credentials
- `.env` excluded from Git
- GitHub secret scanning awareness
- Test-specific configuration for CI

Example:

```properties
groq.api.key=${GROQ_API_KEY}
```

---

# 📊 Supported Workflows

| Workflow | Status |
|---|---|
| Referral Requests | ✅ |
| Recruiter Outreach | ✅ |
| Connection Requests | ✅ |
| LinkedIn Replies | ✅ |
| Follow-Ups | ✅ |
| Fast Replies | ✅ |
| Chrome Extension | ✅ |
| Docker Backend | ✅ |
| Docker Frontend | ✅ |
| Docker Compose | ✅ |
| GitHub Actions CI | 🚧 |

> CI configuration is implemented; final GitHub Actions verification is still being refined.

---

# 💡 Engineering Skills Demonstrated

- Full-Stack Development
- Java & Spring Boot
- REST API Design
- React & Vite
- Material UI
- Chrome Extension Development
- AI/LLM API Integration
- Prompt Engineering
- Docker & Docker Compose
- Nginx
- GitHub Actions
- CI/CD
- API Security
- Environment-Based Configuration
- Maven
- HTTP/WebClient
- Frontend-Backend Integration
- Software Architecture

---

# 👨‍💻 Author

**Prijith John**

Computer Science Engineer

### Areas of Interest

- Backend Engineering
- Full-Stack Development
- AI Applications
- Software Architecture
- Product Development
- Developer Tools

### Connect

- GitHub: https://github.com/prijithjohn
- LinkedIn: https://linkedin.com/in/prijith-john-dev
- Portfolio: https://prijith-portfolio.vercel.app

---

# ⭐ Support

If you found LinkUp AI useful or interesting, consider giving the repository a **⭐ Star** on GitHub.

It helps the project reach more developers and supports continued development.
