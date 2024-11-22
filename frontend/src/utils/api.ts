// utils/api.ts

const VITE_API_DOMAIN = import.meta.env.VITE_API_DOMAIN;

if (!VITE_API_DOMAIN) {
    throw new Error('VITE_API_DOMAIN is not defined');
}

export interface Video {
    id: number;
    title: string;
    user: string;
    thumbnail: string;
}

export interface VideoDetails {
    title: string;
    description: string;
    user: string;
    videoUrl: string;
}

export async function fetchVideos(): Promise<Video[]> {
    console.log('fetchVideos called');
    try {
        const response = await fetch(`${VITE_API_DOMAIN}/api/videos/summaries`);
        console.log('Response status:', response.status); // Estat de la resposta
        if (!response.ok) {
            throw new Error(`Failed to fetch video summaries: ${response.statusText}`);
        }
        const videoSummaries: VideoSummaryDTO[] = await response.json();
        console.log('Fetched data:', videoSummaries); 

        return videoSummaries.map(video => ({
            id: video.id,
            title: video.title,
            user: video.uploaderUsername,
            thumbnail: `${VITE_API_DOMAIN}/api/images/${video.id}.webp`,
        })).sort((a, b) => a.id - b.id);
    } catch (error) {
        console.error('Error fetching video summaries:', error);
        throw error;
    }
}

export async function fetchVideoDetails(id: number): Promise<VideoDetails> {
    try {
        const response = await fetch(`${VITE_API_DOMAIN}/api/videos/${id}/details`);
        if (!response.ok) {
            throw new Error(`Failed to fetch video details: ${response.statusText}`);
        }
        const videoDetails: VideoDetails = await response.json();
        return videoDetails;
    } catch (error) {
        console.error(`Error fetching video details for video ID ${id}:`, error);
        throw error;
    }
}

interface VideoSummaryDTO {
    id: number;
    title: string;
    uploaderUsername: string;
    thumbnailUrl: string;
}