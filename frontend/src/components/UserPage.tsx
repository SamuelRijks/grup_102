import React, { useState } from 'react';
import '../styles/UserPage.css';

interface User {
  username: string;
  email: string;
  password: string;
}

const UserPage: React.FC = () => {
  const [isLogin, setIsLogin] = useState(true);
  const [username, setUsername] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [error, setError] = useState<string | null>(null);

  const toggleForm = () => {
    setIsLogin(!isLogin);
    setError(null);
  };

  const handleRegister = async (e: React.FormEvent) => {
    e.preventDefault();
    if (password !== confirmPassword) {
      setError('Passwords do not match');
      return;
    }

    const user: User = { username, email, password };

    try {
      const response = await fetch('http://localhost:8080/api/auth/register', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(user),
      });

      if (!response.ok) {
        throw new Error('Failed to register');
      }

      // Handle successful registration (e.g., redirect to login page)
      alert('Registration successful!');
      setIsLogin(true);
    } catch (err) {
      setError((err as Error).message);
    }
  };

  return (
    <div className="user-page">
      <div className="form-container">
        {isLogin ? (
          <div className="login-form">
            <h2>Login</h2>
            <form>
              <div className="form-group">
                <label htmlFor="login-email">Email:</label>
                <input type="email" id="login-email" value={email} onChange={(e) => setEmail(e.target.value)} required />
              </div>
              <div className="form-group">
                <label htmlFor="login-password">Password:</label>
                <input type="password" id="login-password" value={password} onChange={(e) => setPassword(e.target.value)} required />
              </div>
              <button type="submit" className="submit-button">Login</button>
            </form>
            <p>
              Don't have an account? <button onClick={toggleForm} className="toggle-button">Register</button>
            </p>
          </div>
        ) : (
          <div className="register-form">
            <h2>Register</h2>
            <form onSubmit={handleRegister}>
              <div className="form-group">
                <label htmlFor="register-username">Username:</label>
                <input
                  type="text"
                  id="register-username"
                  value={username}
                  onChange={(e) => setUsername(e.target.value)}
                  required
                />
              </div>
              <div className="form-group">
                <label htmlFor="register-email">Email:</label>
                <input
                  type="email"
                  id="register-email"
                  value={email}
                  onChange={(e) => setEmail(e.target.value)}
                  required
                />
              </div>
              <div className="form-group">
                <label htmlFor="register-password">Password:</label>
                <input
                  type="password"
                  id="register-password"
                  value={password}
                  onChange={(e) => setPassword(e.target.value)}
                  required
                />
              </div>
              <div className="form-group">
                <label htmlFor="register-confirm-password">Confirm Password:</label>
                <input
                  type="password"
                  id="register-confirm-password"
                  value={confirmPassword}
                  onChange={(e) => setConfirmPassword(e.target.value)}
                  required
                />
              </div>
              {error && <p className="error">{error}</p>}
              <button type="submit" className="submit-button">Register</button>
            </form>
            <p>
              Already have an account? <button onClick={toggleForm} className="toggle-button">Login</button>
            </p>
          </div>
        )}
      </div>
    </div>
  );
};

export default UserPage;