import React, { useEffect, useState } from 'react';
import VideoList from './components/VideoList';
import { fetchVideos, Video } from './utils/api';

const App: React.FC = () => {
  const [videos, setVideos] = useState<Video[]>([]);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    async function loadVideos() {
      try {
        const videoList = await fetchVideos();
        setVideos(videoList);
      } catch (err) {
        if (err instanceof Error) {
          setError(`Failed to load videos: ${err.message}`);
        } else {
          setError('Failed to load videos: Unknown error');
        }
        console.error(err);
      }
    }
    loadVideos();
  }, []);

  return (
    <div className="App">
      <h1>Video List</h1>
      {error && <p>{error}</p>}
      <VideoList videos={videos} />
    </div>
  );
}

export default App;