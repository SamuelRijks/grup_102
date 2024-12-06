import React, { useState, useEffect } from 'react';

interface EditVideoProps {
    videoId: number;
    username: string | null; // Current user's username
    onClose: () => void;
    onVideoUpdated: () => void;
}

const EditVideo: React.FC<EditVideoProps> = ({ videoId, username, onClose, onVideoUpdated }) => {
    const [title, setTitle] = useState<string>('');
    const [description, setDescription] = useState<string>('');
    const [error, setError] = useState<string | null>(null);
    const [isOwner, setIsOwner] = useState<boolean>(false);

    useEffect(() => {
        // Fetch current video details
        async function fetchVideoDetails() {
            try {
                const response = await fetch(`http://localhost:8080/api/videos/${videoId}/details`);
                if (!response.ok) {
                    throw new Error('Failed to load video details');
                }

                const data = await response.json();
                setTitle(data.title);
                setDescription(data.description || '');
                setIsOwner(data.uploaderUsername === username); // Check ownership
            } catch (err) {
                setError('Failed to load video details');
            }
        }

        fetchVideoDetails();
    }, [videoId, username]);

    const handleSave = async () => {
        try {
            console.log("Editing video with username:", username); // Debug username
            const response = await fetch(`http://localhost:8080/api/videos/edit/${videoId}`, {
                method: 'PUT',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({
                    title,
                    description,
                    username, // Include the username
                }),
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

    if (!isOwner) {
        return (
            <div className="edit-video-modal">
                <h2>Access Denied</h2>
                <p>You are not allowed to edit this video.</p>
                <button onClick={onClose}>Close</button>
            </div>
        );
    }

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
