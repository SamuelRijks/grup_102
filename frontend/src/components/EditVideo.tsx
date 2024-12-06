import React, { useState, useEffect } from 'react';

interface EditVideoProps {
    videoId: number;
    onClose: () => void;
    onVideoUpdated: () => void;
}

const EditVideo: React.FC<EditVideoProps> = ({ videoId, onClose, onVideoUpdated }) => {
    const [title, setTitle] = useState<string>('');
    const [description, setDescription] = useState<string>('');
    const [error, setError] = useState<string | null>(null);

    useEffect(() => {
        // Fetch current video details
        async function fetchVideoDetails() {
            try {
                const response = await fetch(`http://localhost:8080/api/videos/${videoId}/details`);
                const data = await response.json();
                setTitle(data.title);
                setDescription(data.meta.description || '');
            } catch (err) {
                setError('Failed to load video details');
            }
        }

        fetchVideoDetails();
    }, [videoId]);

    const handleSave = async () => {
        try {
            const response = await fetch(`http://localhost:8080/api/videos/edit/${videoId}`, {
                method: 'PUT',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ title, description }),
            });

            if (!response.ok) {
                throw new Error('Failed to update video');
            }

            onVideoUpdated();
            onClose();
        } catch (err) {
            setError('Failed to save changes');
        }
    };

    return (
        <div className="edit-video-modal">
            <h2>Edit Video</h2>
            {error && <p className="error">{error}</p>}
            <label>
                Title:
                <input
                    type="text"
                    value={title}
                    onChange={(e) => setTitle(e.target.value)}
                />
            </label>
            <label>
                Description:
                <textarea
                    value={description}
                    onChange={(e) => setDescription(e.target.value)}
                ></textarea>
            </label>
            <button onClick={handleSave}>Save Changes</button>
            <button onClick={onClose}>Cancel</button>
        </div>
    );
};

export default EditVideo;
