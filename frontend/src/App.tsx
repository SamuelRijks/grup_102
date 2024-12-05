import React, { useEffect, useState } from 'react';
import { BrowserRouter as Router, Route, Routes, Link, Navigate } from 'react-router-dom';
import VideoList from './components/VideoList';
import VideoPage from './components/VideoPage';
import UserPage from './components/UserPage';
import MyCommentsPage from './components/MyCommentsPage';
import MyVideosPage from './components/MyVideosPage';
import { fetchVideos, Video } from './utils/api';
import './App.css';
import logo from './assets/logo.png';
import userIcon from './assets/user-icon.png';

const App: React.FC = () => {
  const [videos, setVideos] = useState<Video[]>([]);
  const [error, setError] = useState<string | null>(null);
  const [username, setUsername] = useState<string | null>(null);
  const [showDialog, setShowDialog] = useState(false);

  useEffect(() => {
    async function loadVideos() {
      try {
        const videoList = await fetchVideos();
        setVideos(videoList);
      } catch (err) {
        setError(`Failed to load videos: ${(err as Error).message}`);
        console.error(err);
      }
    }
    loadVideos();
  }, []);

  const handleProfileClick = () => {
    if (username) {
      setShowDialog(!showDialog);
    } else {
      // Redirigir a la página de inicio de sesión si no está autenticado
      window.location.href = '/user';
    }
  };

  const handleLogout = () => {
    setUsername(null);
    localStorage.removeItem('username');
    setShowDialog(false);
  };

  const handleSetUsername = (username: string) => {
    setUsername(username);
    localStorage.setItem('username', username);
  };

  return (
    <Router>
      <div className="App">
        {/* Navbar */}
        <nav className="navbar">
          <div className="nav-content">
            {/* Logo */}
            <div className="logo">
              <Link to="/">
                <img src={logo} alt="Protube Logo" className="logo-img" />
                <h1 className="logo-title">Protube</h1>
              </Link>
            </div>

            {/* Search Bar */}
            <div className="search-bar">
              <input
                type="text"
                placeholder="Search videos..."
                className="search-input"
              />
              <button className="search-button">Search</button>
            </div>

            {/* User Profile */}
            <div className="user-profile">
              <img
                src={userIcon}
                alt="User Profile"
                className="profile-pic"
                onClick={handleProfileClick}
              />
              {username && showDialog && (
                <div className="profile-dialog">
                  <p>Welcome {username}</p>
                  <Link to="/my-comments">My Comments</Link>
                  <Link to="/my-videos">My Videos</Link>
                  <button className="logout" onClick={handleLogout}>Log Out</button>
                </div>
              )}
            </div>
          </div>
        </nav>

        {/* Error Message */}
        {error && <p className="error">{error}</p>}

        {/* Main Content */}
        <Routes>
          <Route path="/" element={<VideoList videos={videos} />} />
          <Route path="/video/:id" element={<VideoPage username={username} />} />
          <Route path="/user" element={<UserPage setUsername={handleSetUsername} />} />
          <Route
            path="/my-comments"
            element={username ? <MyCommentsPage username={username} /> : <Navigate to="/user" />}
          />
          <Route
            path="/my-videos"
            element={username ? <MyVideosPage username={username} /> : <Navigate to="/user" />}
          />
        </Routes>
      </div>
    </Router>
  );
};

export default App;
