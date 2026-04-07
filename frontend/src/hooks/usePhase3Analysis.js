import { useState, useCallback } from 'react';
import { useAuth } from '../context/AuthContext';

/**
 * Phase 3 Hook: Call backend /analyze-github-repo endpoint
 * This endpoint:
 * 1. Fetches repo files from GitHub
 * 2. Runs code analysis
 * 3. Stores results in database
 * 4. Returns health scores & findings
 */
export function usePhase3Analysis() {
  const { jwtToken: token, githubToken } = useAuth();
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [result, setResult] = useState(null);

  const analyze = useCallback(async (owner, repo) => {
    console.log('\n🎯 usePhase3Analysis.analyze() CALLED');
    console.log('─────────────────────────────────────────────────────');
    console.log('  Params - owner:', owner, ', repo:', repo);
    console.log('  token present:', !!token, token?.substring(0, 20));
    console.log('  githubToken present:', !!githubToken, githubToken?.substring(0, 20));
    console.log('─────────────────────────────────────────────────────\n');

    if (!token) {
      const msg = 'Not authenticated. Please login first.';
      console.error('❌', msg);
      setError(msg);
      return null;
    }

    if (!githubToken) {
      const msg = 'GitHub token not configured. Please add your GitHub PAT in Settings.';
      console.error('❌', msg);
      setError(msg);
      return null;
    }

    setLoading(true);
    setError(null);

    try {
      const API_BASE = import.meta.env.VITE_API_URL || 'http://localhost:8000';
      const endpoint = `${API_BASE}/api/repositories/analyze-github-repo`;
      
      console.log('🌐 Backend API Call:');
      console.log('  Endpoint:', endpoint);
      console.log('  Method: POST');
      console.log('  Headers:');
      console.log('    Content-Type: application/json');
      console.log('    Authorization: Bearer ' + token.substring(0, 20) + '...');
      console.log('  Body:', { githubToken: githubToken.substring(0, 20) + '...', owner, repo });

      const response = await fetch(endpoint, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`,
        },
        body: JSON.stringify({
          githubToken,
          owner,
          repo,
        }),
      });

      console.log('📡 Response received:');
      console.log('  Status:', response.status, response.statusText);
      console.log('  OK:', response.ok);

      if (!response.ok) {
        let errorData = {};
        try {
          errorData = await response.json();
        } catch (e) {
          console.warn('  Could not parse error response as JSON');
        }
        const errorMsg = errorData.message || `Analysis failed: ${response.status}`;
        console.error('❌ Backend returned error:', errorMsg);
        throw new Error(errorMsg);
      }

      const data = await response.json();
      console.log('✅ Analysis successful! Result:', data);
      setResult(data);
      return data;
    } catch (err) {
      const msg = err.message || 'Analysis failed';
      console.error('❌ Exception in analyze():', msg);
      console.error('  Full error:', err);
      setError(msg);
      return null;
    } finally {
      setLoading(false);
      console.log('🎯 usePhase3Analysis.analyze() COMPLETE\n');
    }
  }, [token, githubToken]);

  return { analyze, loading, error, result };
}
