
import './App.css'
import { User, MessageCircle, X, Heart} from 'lucide-react';
import React, { useState, useEffect } from 'react';

//const setMatch = () =>{console.log('gar tara')};

const fetchRandomProfile = async () => {
  const response = await fetch('http://localhost:8080/profiles/random');
  if (!response.ok) {
    throw new Error('Failed to fetch profile');
  }
  return response.json();
}

const findMatches = async () => {
  const response = await fetch('http://localhost:8080/matches');
  if (!response.ok) {
    throw new Error('Failed to fetch profile');
  }

  //console.log(response.json());

  return response.json();
}


const saveSwipe = async (profileId) => {
  const response = await fetch('http://localhost:8080/matches', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json'
    },
    body: JSON.stringify({ profileId })
  });
  if (!response.ok) {
    throw new Error('Failed to save swipe');
  }
}

const ProfileSelector = ({ profile, onSwipe }) => (
  profile ? (
  <div className="rounded-lg overflow-hidden bg-white shadow-lg">
    <div className="relative">
      <img src={'http://localhost:8080/images/' + profile.imageUrl} />
      <div className="absolute bottom-0 left-0 right-0 text-white p-4 bg-gradient-to-t from-black">
        <h2 className='text-3xl font-bold'>{profile.firstName} {profile.lastName}, {profile.age}</h2>
      </div>
    </div>
    <div className="p-4">
      <p className="text-gray-600 mb-4">{profile.bio}</p>
    </div>
    <div className="p-4 flex justify-center space-x-4">
      <button className='bg-red-500 rounded-full p-4 text-white hover:bg-red-700'
        onClick={() => onSwipe(profile.id,'left')}>
        <X size={24} />
      </button>
      <button className='bg-green-500 rounded-full p-4 text-white hover:bg-green-700'
        onClick={() => onSwipe(profile.id, 'right')}>
        <Heart size={24} />
      </button>
    </div>
  </div>
  ) : (<div>Loading...</div>)
);

const MatchesList = ({onSelectMatch}) => {

  return (

  <div className="rounded-lg shadow-lg p-4">
    <h2 className="text-2xl font-bold mb-4">Matches</h2>
    <ul>
    {[
      {id:'1', firstName:'Hanah', lastName:'Alisson', age:'30', imageUrl:'http://localhost:8080/images/7e19ab66-fc54-4b2f-89e1-60c852961ef7.jpg'},
      {id:'2', firstName:'Nina', lastName:'Potinaya', age:'20', imageUrl:'http://localhost:8080/images/11d7f6dc-3164-4e1f-890f-f6e10c031d5d.jpg'}

    ].map(match => (

            <li key={match.id} className="mb-2"> 
      <button className="w-full hover:bg-gray-100 rounded flex item-center" onClick={() => onSelectMatch(match)} >
          <img src={match.imageUrl} className="w-16 h-16 rounded-full mr-3 object-cover"></img>
          <span>
            <h3 className='font-bold'>
              {match.firstName} {match.lastName}
            </h3>
          </span>
      </button> 
      </li>
      
    ))
    }
    </ul>
  </div>
  );
  }


const ChatScreen = ({match}) => {

  return (
  <div className="rounded-lg shadow-lg p-4">
    <h2 className="text-2xl font-bold mb-4">Chat</h2>
  
    {[
      "Hi, How are You Today,"
    ].map((message, index) => (
      <div key={index}>
        <div>
          {message} {match.firstName} ?
        </div>
      </div> 
    ))
    }
    </div>
  );
}




function App() {

  const [currentScreen, setCurrentScreen] = useState('profile');
  const [currentProfile, setCurrentProfile] = useState(null);
  const [currentMatch, setCurrentMatch] = useState('match');


  const onSelectMatch = (match) => { 
      
      //const [currenMatch, setCurrenMatch] = useState('match');
      setCurrentMatch(match);
      setCurrentScreen('chat');
  }    

  const onSwipe = (profileId,direction) => {
    if (direction === 'right') {
      console.log(profileId + ' Liked');
      saveSwipe(profileId)
    }

    if (direction === 'left') {
      console.log('Dislike');
    }

    loadRandomProfile();
  }


  const loadRandomProfile = async () => {
    try {
      const profile = await fetchRandomProfile();
      setCurrentProfile(profile);

    } catch (error) {
      console.error(error);
    }
  }

  const loadMatches = async () => {
    try {
      const matches = await findMatches();
      console.log(matches);

    } catch (error) {
      console.error(error);
    }
  }

  const renderScreen = () => {
    switch (currentScreen) {
      case 'profile':
        return <ProfileSelector profile={currentProfile} onSwipe={onSwipe}/>;
      case 'matches':
        return <MatchesList  onSelectMatch={onSelectMatch}/>;
      case 'chat':
          return <ChatScreen match={currentMatch}/>;   
    }
  }


  useEffect(() => {
    loadRandomProfile();
    loadMatches();
  }, {});

  return (
    <div className="max-w-md mx-auto p-4">
    <nav className="flex justify-between mb-4">
      <User onClick={() => setCurrentScreen('profile')}/>
      <MessageCircle onClick={() => setCurrentScreen('matches')}/>
    </nav>
    {renderScreen()}
  </div>
  )
}

export default App
