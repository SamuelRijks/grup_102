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

export async function fetchVideos(): Promise<Video[]> {
    try {
        // Step 1: Fetch the list of files (JSON, MP4, WEBP) from the main endpoint
        const response = await fetch(`${VITE_API_DOMAIN}/api/videos`);
        if (!response.ok) {
            throw new Error(`Failed to fetch videos: ${response.statusText}`);
        }
        const fileList: string[] = await response.json();

        // Step 2: Filter and create a set of unique video IDs
        const videoIds = new Set<number>();
        fileList.forEach(file => {
            const match = file.match(/^(\d+)\.json$/); // Match JSON files to extract video IDs
            if (match) videoIds.add(parseInt(match[1]));
        });

        // Step 3: For each video ID, fetch its JSON metadata and construct the video object
        const videos: Video[] = await Promise.all(
            Array.from(videoIds).map(async (id) => {
                const metadataResponse = await fetch(`${VITE_API_DOMAIN}/media/${id}.json`);
                if (!metadataResponse.ok) {
                    throw new Error(`Failed to fetch video metadata for video ID ${id}`);
                }
                const metadata = await metadataResponse.json();

                return {
                    id: metadata.id,
                    title: metadata.title,
                    user: metadata.user,
                    thumbnail: `${VITE_API_DOMAIN}/media/${id}.webp`, // URL for the thumbnail
                };
            })
        );

        // Step 4: Sort the videos by ID and return
        return videos.sort((a, b) => a.id - b.id);

    } catch (error) {
        console.error('Error fetching videos:', error);
        throw error;
    }
}
