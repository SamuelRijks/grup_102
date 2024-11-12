import React, { useEffect, useState } from 'react';
import VideoList from './components/VideoList';
import VideoPlayer from './components/VideoPlayer';
import { fetchVideos, Video } from './utils/api';

const App: React.FC = () => {
  const [videos, setVideos] = useState<Video[]>([]);
  const [selectedVideo, setSelectedVideo] = useState<Video | null>(null);
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

  const handleVideoSelect = (video: Video) => {
    setSelectedVideo(video);
  };

  return (
    <div className="App">
      <h1>Protube</h1>
      {error && <p>{error}</p>}
      {selectedVideo ? (
        <VideoPlayer video={selectedVideo} onClose={() => setSelectedVideo(null)} />
      ) : (
        <VideoList videos={videos} onVideoSelect={handleVideoSelect} />
      )}
    </div>
  );
}

export default App;