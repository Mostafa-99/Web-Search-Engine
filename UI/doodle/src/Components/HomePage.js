import { useState } from "react";
import { Route, BrowserRouter as Router } from "react-router-dom";
import logo from '../hadras.png'
import './HomePage.css'
function HomePage() {
    const [text,setText] = useState('');
    function mySubmitHandler(){
        localStorage.setItem('text', text);
        localStorage.setItem('currentPage', 1);
    }
  return (
    <div className="home-page">
        {/* <a href="https://fontmeme.com/google-font/"><img src="https://fontmeme.com/permalink/210524/5a66607532bca256c144bd8a3cd07d16.png" alt="google-font" border="0"></a> */}
        <div className="home-hadras-logo">
            <a href=""><img style={{height:'200px'}} src={logo} alt="hadras" border="0"/></a>
        </div>
        <div className="home-logo">
            <a href=""><img src="https://fontmeme.com/permalink/210524/c504e219e1b6c0a480ba44921ad51c52.png" alt="google-font" border="0"/></a>
        </div>
        <div className="home-textbox">
            <form action="/doodling" onSubmit={event=>mySubmitHandler()}>
                <input
                    autoFocus
                    placeholder="   Enter search text..."
                    type='text'
                    onChange={event=>setText(event.target.value)}
                    className="home-input-text"
                />
            </form>
        </div>
    </div>
  );
}

export default HomePage;
