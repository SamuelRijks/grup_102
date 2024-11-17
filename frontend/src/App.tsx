import React, { useEffect, useState } from 'react';
import { BrowserRouter as Router, Route, Routes, useNavigate } from 'react-router-dom';
import VideoList from './components/VideoList';
import VideoPlayer from './components/VideoPlayer';
import { fetchVideos, fetchVideoDetails, Video, VideoDetails } from './utils/api';

const App: React.FC = () => {
  const [videos, setVideos] = useState<Video[]>([]);
  const [selectedVideoDetails, setSelectedVideoDetails] = useState<VideoDetails | null>(null);
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

  const handleVideoSelect = async (videoId: number) => {
    try {
      const videoDetails = await fetchVideoDetails(videoId);
      setSelectedVideoDetails(videoDetails);
    } catch (err) {
      setError(`Failed to load video details: ${(err as Error).message}`);
      console.error(err);
    }
  };

  return (
    <Router>
      <div className="App">
        <h1>Protube</h1>
        {error && <p>{error}</p>}
        <Routes>
          <Route path="/" element={<VideoList videos={videos} onVideoSelect={handleVideoSelect} />} />
          <Route path="/video" element={selectedVideoDetails && <VideoPlayer videoDetails={selectedVideoDetails} onClose={() => setSelectedVideoDetails(null)} />} />
        </Routes>
      </div>
    </Router>
  );
}

export default App;