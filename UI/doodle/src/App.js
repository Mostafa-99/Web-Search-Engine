import './App.css';
import { Route, BrowserRouter as Router, Switch } from "react-router-dom";
import HomePage from './Components/HomePage'
import SearchingPage from './Components/SearchingPage'

function App() {
  return (
    <div className="App"  style={{height:"100%"}}>
      <Switch>
        <Route path="/doodle" component={HomePage} exact/>
        <Route path="/doodling" component={SearchingPage} exact/>
      </Switch>
    </div>
  );
}

export default App;
