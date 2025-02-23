import React from 'react';
import { Link } from 'react-router-dom';

// Sample data for drafts
const drafts = [
  {
    id: 1,
    title: 'Bug Report #1: Login Issue',
    lastModified: 'Feb 18, 2025, 11:15 AM',
  },
  {
    id: 2,
    title: 'Bug Report #2: Dashboard Layout Bug',
    lastModified: 'Feb 17, 2025, 4:30 PM',
  },
  {
    id: 3,
    title: 'Bug Report #3: UI Glitch on Mobile',
    lastModified: 'Feb 16, 2025, 9:45 AM',
  },
];

const SavedDraftsPage = () => {
  return (
    <div style={styles.container}>
      {/* Header with Back Button */}
      <header style={styles.header}>
        <Link to="/dashboard" style={styles.backLink}>
          <span style={styles.arrow}>&larr;</span> Bug Board
        </Link>
      </header>

      {/* Main Content */}
      <main style={styles.main}>
        <h1 style={styles.title}>Saved Draft</h1>
        <ul style={styles.draftList}>
          {drafts.map((draft) => (
            <li key={draft.id} style={styles.draftItem}>
              <span style={styles.bugTitle}>{draft.title}</span>
              <span style={styles.lastModified}>
                Last Modified: {draft.lastModified}
              </span>
            </li>
          ))}
        </ul>
      </main>
    </div>
  );
};

const styles = {
  container: {
    fontFamily: '"Poppins", sans-serif',
    backgroundColor: '#f5f5f5',
    color: '#333',
    minHeight: '100vh',
  },
  header: {
    backgroundColor: '#fff',
    padding: '20px',
    boxShadow: '0 2px 4px rgba(0, 0, 0, 0.1)',
  },
  backLink: {
    textDecoration: 'none',
    fontSize: '18px',
    color: '#007bff',
    display: 'flex',
    alignItems: 'center',
  },
  arrow: {
    marginRight: '8px',
    fontSize: '20px',
  },
  main: {
    padding: '20px',
  },
  title: {
    marginTop: 0,
    fontSize: '28px',
    fontWeight: 'bold',
  },
  draftList: {
    listStyle: 'none',
    padding: 0,
    margin: '20px 0',
  },
  draftItem: {
    backgroundColor: '#fff',
    padding: '15px',
    border: '1px solid #ddd',
    borderRadius: '4px',
    marginBottom: '10px',
    display: 'flex',
    justifyContent: 'space-between',
    alignItems: 'center',
    transition: 'background-color 0.2s',
  },
  bugTitle: {
    fontSize: '18px',
    fontWeight: '500',
  },
  lastModified: {
    fontSize: '14px',
    color: '#666',
  },
};

export default SavedDraftsPage;
