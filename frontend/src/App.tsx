import React, { useEffect, useState } from 'react';
import { BrowserRouter as Router, Route, Routes, useNavigate } from 'react-router-dom';
import VideoList from './components/VideoList';
import VideoPage from './components/VideoPage';
import UserProfile from './components/UserProfile';
import { fetchVideos, Video } from './utils/api';
import './App.css';
import logo from './assets/logo.png';
import userIcon from './assets/user-icon.png';

const App: React.FC = () => {
  const [videos, setVideos] = useState<Video[]>([]);
  const [error, setError] = useState<string | null>(null);

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

  const addVideo = (newVideo: Video) => {
    setVideos((prevVideos) => [newVideo, ...prevVideos]); // Add the video to the top
};

  const Navbar: React.FC = () => {
    const navigate = useNavigate();
    const userId = "13000";
    const handleProfileClick = () => {
      navigate(`/profile/${userId}`); // Redirigeix a la pàgina de perfil
    };

    return (
      <nav className="navbar">
        <div className="nav-content">
          {/* Logo */}
          <div className="logo">
            <img src={logo} alt="Protube Logo" className="logo-img" />
            <h1>Protube</h1>
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
          <div className="user-profile" onClick={handleProfileClick}>
            <img src={userIcon} alt="User Profile" className="profile-pic" />
          </div>
        </div>
      </nav>
    );
  };

  return (
    <Router>
      <div className="App">
        <Navbar />
        {/* Error Message */}
        {error && <p className="error">{error}</p>}

        {/* Main Content */}
        <Routes>
          <Route path="/" element={<VideoList videos={videos} />} />
          <Route path="/video/:id" element={<VideoPage />} />
          <Route path="/profile/:userId"  element={<UserProfile addVideo={addVideo} />} /> {/* Nova ruta */}
        </Routes>
      </div>
    </Router>
  );
};

export default App;
