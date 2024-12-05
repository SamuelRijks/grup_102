import React, { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import '../styles/MyCommentsPage.css';

interface Comment {
    id: number;
    content: string;
    videoTitle: string;
    videoId: number;
}

interface MyCommentsPageProps {
    username: string | null;
}

const MyCommentsPage: React.FC<MyCommentsPageProps> = ({ username }) => {
    const [comments, setComments] = useState<Comment[]>([]);
    const [error, setError] = useState<string | null>(null);
    const [loading, setLoading] = useState<boolean>(true);

    useEffect(() => {
        if (username) {
            async function fetchComments() {
                try {
                    const response = await fetch(`http://localhost:8080/api/users/${username}/comments`);
                    if (!response.ok) {
                        throw new Error('Failed to fetch comments');
                    }
                    const data = await response.json();
                    console.log('Comments received:', data);

                    // Transform the data to the desired format
                    const transformedComments = data.map((comment: any) => ({
                        id: comment.id,
                        content: comment.content,
                        videoTitle: comment.videoTitle,
                        videoId: comment.videoId,
                    }));

                    setComments(transformedComments);
                } catch (err) {
                    setError((err as Error).message);
                } finally {
                    setLoading(false);
                }
            }
            fetchComments();
        }
    }, [username]);

    if (!username) {
        return <p className="centered-message">Please log in to see your comments.</p>;
    }

    if (loading) {
        return <p className="centered-message">Loading comments...</p>;
    }

    if (error) {
        return <p className="centered-message error">{error}</p>;
    }

    if (comments.length === 0) {
        return <p className="centered-message">You haven't posted any comments yet.</p>;
    }

    return (
        <div className="my-comments-page">
            <h2>My Comments</h2>
            <ul>
                {comments.map((comment) => (
                    <li key={comment.id}>
                        <p>{comment.content}</p>
                        <p>Posted on: <Link to={`/video/${comment.videoId}`}>{comment.videoTitle}</Link></p>
                    </li>
                ))}
            </ul>
        </div>
    );
};

export default MyCommentsPage;