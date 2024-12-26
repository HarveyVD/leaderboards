class QuizApp {
    constructor() {
        this.baseUrl = config.apiUrl;
        this.wsBaseUrl = config.wsUrl;
        this.token = localStorage.getItem('token');
        this.user = JSON.parse(localStorage.getItem('user'));
        this.currentQuiz = JSON.parse(localStorage.getItem('currentQuiz'));
        this.ws = null;

        this.initializeElements();
        this.attachEventListeners();
        this.checkAuth();
    }

    initializeElements() {
        this.loginForm = document.getElementById('loginForm');
        this.quizList = document.getElementById('quizList');
        this.leaderboard = document.getElementById('leaderboard');
        this.usernameInput = document.getElementById('username');
        this.quizSearchInput = document.getElementById('quizSearch');
        this.scoreInput = document.getElementById('scoreInput');
        this.loginBtn = document.getElementById('loginBtn');
        this.logoutBtn = document.getElementById('logoutBtn');
        this.searchQuizBtn = document.getElementById('searchQuizBtn');
        this.backToQuizList = document.getElementById('backToQuizList');
        this.submitScore = document.getElementById('submitScore');
        this.quizListContent = document.getElementById('quizListContent');
        this.leaderboardContent = document.getElementById('leaderboardContent');
        this.userNameDisplay = document.getElementById('userNameDisplay');
        this.userNameLeaderboard = document.getElementById('userNameLeaderboard');
        this.quizTopicDisplay = document.getElementById('quizTopicDisplay');
    }

    attachEventListeners() {
        this.loginBtn.addEventListener('click', () => this.login());
        this.logoutBtn.addEventListener('click', () => this.logout());
        this.searchQuizBtn.addEventListener('click', () => this.searchAndSelectQuiz());
        this.backToQuizList.addEventListener('click', () => this.showQuizList());
        this.submitScore.addEventListener('click', () => this.submitUserScore());
        this.quizSearchInput.addEventListener('keypress', (e) => {
            if (e.key === 'Enter') {
                this.searchAndSelectQuiz();
            }
        });
    }

    checkAuth() {
        if (this.token && this.user) {
            this.updateUserDisplay();
            if (this.currentQuiz) {
                this.quizTopicDisplay.textContent = `Leaderboard: ${this.currentQuiz.topic}`;
                this.showLeaderboard();
                this.connectWebSocket();
            } else {
                this.showQuizList();
                this.loadQuizzes();
            }
        } else {
            this.showLoginForm();
        }
    }

    updateUserDisplay() {
        if (this.user) {
            this.userNameDisplay.textContent = this.user.username;
            this.userNameLeaderboard.textContent = this.user.username;
        }
    }

    handleAuthError(response, error) {
        if (response.status === 401 || response.status === 403) {
            console.log('Authentication failed:', response.status);
            this.logout();
            return true;
        }
        return false;
    }

    async fetchWithAuth(url, options = {}) {
        try {
            const response = await fetch(url, options);
            if (this.handleAuthError(response)) return null;
            
            const data = await response.json();
            return { response, data };
        } catch (error) {
            console.error('Fetch error:', error);
            throw error;
        }
    }

    async login() {
        try {
            const username = this.usernameInput.value;
            if (!username) return alert('Please enter username');

            const { response, data } = await this.fetchWithAuth(`${this.baseUrl}/auth/login`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({ username })
            });

            if (response.ok) {
                this.token = data.token;
                this.user = data.user;
                localStorage.setItem('token', this.token);
                localStorage.setItem('user', JSON.stringify(this.user));
                this.updateUserDisplay();
                this.showQuizList();
                this.loadQuizzes();
            } else {
                alert('Login failed');
            }
        } catch (error) {
            console.error('Login error:', error);
            alert('Error occurred during login');
        }
    }

    logout() {
        this.token = null;
        this.user = null;
        this.currentQuiz = null;
        localStorage.removeItem('token');
        localStorage.removeItem('user');
        localStorage.removeItem('currentQuiz');
        this.closeWebSocket();
        this.showLoginForm();
    }

    async loadQuizzes() {
        try {
            const { data } = await this.fetchWithAuth(`${this.baseUrl}/quizzes`, {
                headers: {
                    'x-auth-token': this.token
                }
            });

            if (data && data.status === "SUCCESS" && Array.isArray(data.result)) {
                this.displayQuizzes(data.result);
            } else {
                throw new Error('Invalid response format');
            }
        } catch (error) {
            console.error('Load quizzes error:', error);
            alert('Unable to load quiz list');
        }
    }

    displayQuizzes(quizzes) {
        const quizListHtml = `
            <table class="table">
                <thead>
                    <tr>
                        <th>ID</th>
                        <th>Quiz Name</th>
                        <th>Created Date</th>
                    </tr>
                </thead>
                <tbody>
                    ${quizzes.map(quiz => {
                        const createdDate = new Date(quiz.createdAt).toLocaleString('en-US');
                        return `
                            <tr>
                                <td>${quiz.id}</td>
                                <td>
                                    <a href="#" class="quiz-title" onclick="app.selectQuiz('${quiz.id}'); return false;">
                                        ${quiz.topic}
                                    </a>
                                </td>
                                <td>${createdDate}</td>
                            </tr>
                        `;
                    }).join('')}
                </tbody>
            </table>
        `;
        this.quizListContent.innerHTML = quizListHtml;
    }

    async selectQuiz(quizId) {
        try {
            const { data } = await this.fetchWithAuth(`${this.baseUrl}/users/quizzes/${quizId}`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'x-auth-token': this.token
                }
            });

            if (data && data.status === "SUCCESS" && data.result) {
                this.currentQuiz = { 
                    id: data.result.quiz.id,
                    topic: data.result.quiz.topic,
                    leaderboardId: data.result.id 
                };
                localStorage.setItem('currentQuiz', JSON.stringify(this.currentQuiz));
                this.quizTopicDisplay.textContent = `Leaderboard: ${this.currentQuiz.topic}`;
                this.showLeaderboard();
                this.connectWebSocket();
            } else {
                throw new Error('Invalid response format');
            }
        } catch (error) {
            console.error('Select quiz error:', error);
            alert('Unable to join this quiz');
        }
    }

    handleWebSocketError(error, source = '') {
        console.error(`${source} error:`, error);
        try {
            const response = typeof error.body === 'string' ? JSON.parse(error.body) : 
                           typeof error.data === 'string' ? JSON.parse(error.data) : error;
            
            if (response.status === "FAILED" && response.errorCode === "error.forbidden") {
                console.log(`${source} authentication failed:`, response);
                this.logout();
                return true;
            }
        } catch (e) {
            console.error(`Error parsing ${source} error:`, e);
        }
        return false;
    }

    connectWebSocket() {
        this.closeWebSocket();
        const socket = new SockJS(`${this.baseUrl}/ws?authToken=${this.token}`);
        this.stompClient = Stomp.over(socket);
        
        this.stompClient.connect({}, (frame) => {
            console.log('Connected: ' + frame);
            
            this.stompClient.subscribe(`/queue/leaderboards/${this.currentQuiz.id}/notifications`, (message) => {
                console.log('Received message:', message);
                const data = JSON.parse(message.body);
                if (data.status === "SUCCESS" && Array.isArray(data.result)) {
                    this.updateLeaderboard(data.result);
                } else {
                    this.handleWebSocketError(data, 'Message');
                }
            }, {
                'headers': {'x-auth-token': this.token},
                'onError': (error) => this.handleWebSocketError(error, 'Subscription')
            });

            this.loadInitialLeaderboard();
        }, (error) => this.handleWebSocketError(error, 'Connection'));

        socket.onerror = (error) => this.handleWebSocketError(error, 'Socket');
    }

    closeWebSocket() {
        if (this.stompClient) {
            this.stompClient.disconnect();
            this.stompClient = null;
        }
    }

    async loadInitialLeaderboard() {
        try {
            if (!this.currentQuiz) {
                throw new Error('Quiz information not found');
            }

            const { data } = await this.fetchWithAuth(`${this.baseUrl}/leaderboards/top/10?quiz_id=${this.currentQuiz.id}`, {
                headers: {
                    'x-auth-token': this.token
                }
            });

            if (data && data.status === "SUCCESS" && Array.isArray(data.result)) {
                this.updateLeaderboard(data.result);
            } else {
                throw new Error('Invalid response format');
            }
        } catch (error) {
            console.error('Load initial leaderboard error:', error);
            alert('Unable to load leaderboard');
        }
    }

    updateLeaderboard(leaderboardData) {
        this.leaderboardContent.innerHTML = leaderboardData.map((item, index) => {
            const isCurrentUser = item.user.id === this.user.id;
            return `
                <tr>
                    <td>${index + 1}</td>
                    <td ${isCurrentUser ? 'class="fw-bold"' : ''}>${item.user.username}</td>
                    <td>${item.score}</td>
                </tr>
            `;
        }).join('');
    }

    async submitUserScore() {
        try {
            const score = parseInt(this.scoreInput.value);
            const scoreError = document.getElementById('scoreError');
            
            if (isNaN(score)) {
                scoreError.textContent = 'Please enter a valid score';
                return;
            }
            
            if (score < 1 || score > 100) {
                scoreError.textContent = 'Score must be between 1 and 100';
                return;
            }

            scoreError.textContent = ''; // Clear error message
            if (!this.currentQuiz) {
                scoreError.textContent = 'Quiz information not found';
                return;
            }

            const { response } = await this.fetchWithAuth(`${this.baseUrl}/users/${this.user.id}/score`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'x-auth-token': this.token
                },
                body: JSON.stringify({ 
                    score: score,
                    quiz_id: this.currentQuiz.id
                })
            });

            if (response && response.ok) {
                this.scoreInput.value = '';
                scoreError.textContent = '';
            } else {
                scoreError.textContent = 'Unable to submit score';
            }
        } catch (error) {
            console.error('Submit score error:', error);
            document.getElementById('scoreError').textContent = 'Error occurred while submitting score';
        }
    }

    showLoginForm() {
        this.loginForm.classList.remove('d-none');
        this.quizList.classList.add('d-none');
        this.leaderboard.classList.add('d-none');
    }

    showQuizList() {
        this.loginForm.classList.add('d-none');
        this.quizList.classList.remove('d-none');
        this.leaderboard.classList.add('d-none');
        this.closeWebSocket();
        this.currentQuiz = null;
        localStorage.removeItem('currentQuiz');
        this.loadQuizzes();
    }

    showLeaderboard() {
        this.loginForm.classList.add('d-none');
        this.quizList.classList.add('d-none');
        this.leaderboard.classList.remove('d-none');
    }

    async searchAndSelectQuiz() {
        const searchTerm = this.quizSearchInput.value.trim();
        if (!searchTerm) {
            alert('Please enter quiz name');
            return;
        }

        try {
            const { data } = await this.fetchWithAuth(`${this.baseUrl}/quizzes`, {
                headers: {
                    'x-auth-token': this.token
                }
            });

            if (data && data.status === "SUCCESS" && Array.isArray(data.result)) {
                const quiz = data.result.find(q => q.topic.toLowerCase() === searchTerm.toLowerCase());
                if (quiz) {
                    this.quizSearchInput.value = '';
                    this.selectQuiz(quiz.id);
                } else {
                    alert('Quiz not found');
                }
            } else {
                throw new Error('Invalid response format');
            }
        } catch (error) {
            console.error('Search quiz error:', error);
            alert('Error occurred while searching quiz');
        }
    }
}

const app = new QuizApp();
