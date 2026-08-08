import { Box, Container } from '@mui/system';
import './App.css';
import {
  Button,
  CircularProgress,
  FormControl,
  InputLabel,
  MenuItem,
  Select,
  TextField,
  Typography,
  CssBaseline
} from '@mui/material';
import { useState } from 'react';
import axios from 'axios';
import { TypeAnimation } from 'react-type-animation';
import { ThemeProvider, createTheme } from '@mui/material/styles';


const normalizeAction = (action) => {
  switch (action) {
    case "LinkedIn Follow-up":
      return "REFERRAL_REQUEST";
    case "Cold Pitching Note":
      return "COLD_PITCH";
    case "LinkedIn Reply":
      return "LINKEDIN_REPLY";
    case "Connection Request":
      return "CONNECTION_REQUEST";
    default:
      return "LINKEDIN_REPLY";
  }
};

function App() {
  const [emailContent, setEmailContent] = useState('');
  const [tone, setTone] = useState('Confident');
  const [generatedReply, setGeneratedReply] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [copied, setCopied] = useState(false);
  const [action, setAction] = useState('LinkedIn Follow-up');
  const [darkMode, setDarkMode] = useState(false);

  const [recipientName, setRecipientName] = useState('');
  const [targetRole, setTargetRole] = useState('');
  const [targetCompany, setTargetCompany] = useState('');

  const apiBaseUrl = import.meta.env.VITE_API_URL?.trim() || '';
  const apiEndpoint = `${apiBaseUrl.replace(/\/$/, '')}/api/linkedin/generate`;

  const handleSubmit = async () => {
    setLoading(true);
    setError('');
    setCopied(false);

    const cleanedContext = emailContent?.trim();

    if (!cleanedContext && normalizeAction(action) !== "CONNECTION_REQUEST") {
      setError(
        action === 'LinkedIn Reply'
          ? "Please paste your prior chat logs."
          : "Please paste job description / context."
      );
      setLoading(false);
      return;
    }

    try {
      const payload = {
        emailContent: cleanedContext || "",
        tone,
        action: normalizeAction(action),
        recipientName,
        targetRole,
        targetCompany
      };

      console.log("Submitting request", payload);

      const response = await axios.post(
        apiEndpoint,
        payload,
        {
          headers: {
            'Content-Type': 'application/json'
          }
        }
      );

      console.log("API response", response);
      setGeneratedReply(response.data?.reply || "");
    } catch (err) {
      console.error("Remote API error", err);
      const backendError =
        err?.response?.data?.message ||
        err?.response?.data?.error ||
        err?.response?.data?.reply ||
        err?.message ||
        "Failed to generate response.";
      setError(backendError);
    } finally {
      setLoading(false);
    }
  };

  const handleClear = () => {
    setEmailContent('');
    setTone('Confident');
    setGeneratedReply('');
    setError('');
    setRecipientName('');
    setTargetRole('');
    setTargetCompany('');
  };

  const theme = createTheme({
    palette: {
      mode: darkMode ? 'dark' : 'light',
      background: {
        default: darkMode ? '#121212' : '#f5f5f5',
        paper: darkMode ? '#1e1e1e' : '#ffffff'
      }
    }
  });

  return (
    <ThemeProvider theme={theme}>
      <CssBaseline />

      <Container maxWidth="md" sx={{ py: 4, minHeight: '100vh' }}>

        {/* HEADER */}
        <Typography variant="h3" fontWeight={800}>
          🚀 LinkUp AI Studio
        </Typography>

        <Typography color="text.secondary" sx={{ mb: 2 }}>
          AI-powered LinkedIn messaging system
        </Typography>

        <Button
          variant="outlined"
          onClick={() => setDarkMode(!darkMode)}
          sx={{ mb: 3 }}
        >
          {darkMode ? "Light Mode" : "Dark Mode"}
        </Button>

        {/* ACTION SELECTOR */}
        <FormControl fullWidth sx={{ mb: 3 }}>
          <InputLabel>Goal</InputLabel>
          <Select
            value={action}
            label="Goal"
            onChange={(e) => {
              setAction(e.target.value);
              setEmailContent('');
              setGeneratedReply('');
              setError('');
            }}
          >
            <MenuItem value="LinkedIn Follow-up">Referral Request</MenuItem>
            <MenuItem value="Cold Pitching Note">Cold Pitch</MenuItem>
            <MenuItem value="LinkedIn Reply">Chat Reply</MenuItem>
            <MenuItem value="Connection Request">Connection Invite</MenuItem>
          </Select>
        </FormControl>

        {/* INPUTS */}
        <Box sx={{ display: 'flex', flexDirection: 'column', gap: 2 }}>

          <TextField
          label={
  action === "LinkedIn Follow-up"
    ? "Employee Name"
    : "Recipient Name"
}
            value={recipientName}
            onChange={(e) => setRecipientName(e.target.value)}
          />

          {action !== "LinkedIn Reply" && (
            <TextField
              label="Company"
              value={targetCompany}
              onChange={(e) => setTargetCompany(e.target.value)}
            />
          )}

          {action !== "Connection Request" && (
            <TextField
              multiline
              rows={5}
              label={action === "LinkedIn Reply" ? "Chat History" : "Job Description"}
              value={emailContent}
              onChange={(e) => setEmailContent(e.target.value)}
            />
          )}
        </Box>

        {/* TONE */}
        <FormControl fullWidth sx={{ mt: 2 }}>
          <InputLabel>Tone</InputLabel>
          <Select value={tone} onChange={(e) => setTone(e.target.value)}>
            <MenuItem value="Confident">Confident</MenuItem>
            <MenuItem value="Professional">Professional</MenuItem>
            <MenuItem value="Friendly">Friendly</MenuItem>
            <MenuItem value="Concise">Concise</MenuItem>
          </Select>
        </FormControl>

        {/* BUTTON */}
        <Button
          fullWidth
          variant="contained"
          sx={{ mt: 3 }}
          disabled={loading || (!emailContent && normalizeAction(action) !== "CONNECTION_REQUEST")}
          onClick={handleSubmit}
        >
          {loading ? (
            <>
              <CircularProgress size={20} sx={{ mr: 1 }} />
              Generating...
            </>
          ) : (
            "Generate Reply"
          )}
        </Button>

        {/* ERROR */}
        {error && (
          <Typography color="error" sx={{ mt: 2 }}>
            {error}
          </Typography>
        )}

        {/* OUTPUT */}
        {generatedReply && (
          <Box sx={{ mt: 4, p: 3, borderRadius: 2, bgcolor: darkMode ? '#222' : '#fff' }}>

            <Typography fontWeight={700}>Generated Reply</Typography>

            <Box sx={{ mt: 2, p: 2, bgcolor: '#111', color: '#fff', borderRadius: 2 }}>
              <TypeAnimation
                sequence={[generatedReply]}
                speed={99}
                cursor
              />
            </Box>

            <Box sx={{ display: 'flex', gap: 2, mt: 2 }}>
              <Button
                onClick={() => navigator.clipboard.writeText(generatedReply)}
              >
                Copy
              </Button>

              <Button onClick={handleSubmit}>
                Regenerate
              </Button>

              <Button color="error" onClick={handleClear}>
                Clear
              </Button>
            </Box>
          </Box>
        )}
      </Container>
    </ThemeProvider>
  );
}

export default App;
