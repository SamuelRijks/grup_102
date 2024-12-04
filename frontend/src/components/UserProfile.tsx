import React, { useState } from 'react';
import '../styles/UserProfile.css';
import { uploadVideo, Video } from '../utils/api';

interface UserProfileProps {
  addVideo: (newVideo: Video) => void; // Define the addVideo prop
}

const UserProfile: React.FC<UserProfileProps> = ({ addVideo }) => {
  const [newVideo, setNewVideo] = useState({
    title: '',
    file: null as File | null,
  });
  const [error, setError] = useState<string | null>(null);

  const handleFileChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0] || null;
    setNewVideo({ ...newVideo, file });
  };

  const handleCreateVideo = async (e: React.FormEvent) => {
    e.preventDefault();
    console.log('Form submitted'); // Log when the form is submitted

    if (!newVideo.file) {
        console.log('No file selected'); // Log if no file is selected
        setError('Has de seleccionar un fitxer de vídeo.');
        return;
    }

    try {
        console.log('Preparing to upload video'); // Log before preparing the videoData
        const videoData = {
            title: newVideo.title,
            file: newVideo.file,
            userId: 13000, // Replace with the actual user ID
        };

        console.log('Sending video to upload API'); // Log before API call
        const uploadedVideo = await uploadVideo(videoData);

        console.log('Hi'); // Log success message
        console.log('Video uploaded successfully:', uploadedVideo);

        const formattedVideo: Video = {
            id: uploadedVideo.id,
            title: uploadedVideo.title,
            user: `8000`, // Replace with dynamic username if available
            thumbnail: `${import.meta.env.VITE_API_DOMAIN}/api/images/${uploadedVideo.id}.webp`,
        };

        addVideo(formattedVideo);
        setNewVideo({ title: '', file: null });
        setError(null);
    } catch (error: any) {
        console.error('Error uploading video:', error.message || error);
        setError('Error pujant el vídeo.');
    }
};

  return (
    <div className="user-profile-page">
      <h1>Perfil d'Usuari</h1>
      <section className="create-video-section">
        <h2>Pujar Vídeo</h2>
        {error && <p className="error">{error}</p>}
        <form onSubmit={handleCreateVideo}>
          <label>
            Títol:
            <input
              type="text"
              value={newVideo.title}
              onChange={(e) => setNewVideo({ ...newVideo, title: e.target.value })}
              required
            />
          </label>
          <label>
            Fitxer del Vídeo:
            <input
              type="file"
              accept="video/mp4"
              onChange={handleFileChange}
              required
            />
          </label>
          <button type="submit">Crear Vídeo</button>
        </form>
      </section>
    </div>
  );
};

export default UserProfile;
