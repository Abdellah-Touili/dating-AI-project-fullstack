
import './App.css'
import { User, MessageCircle, X, Heart, Send} from 'lucide-react';
import React, { useState, useEffect } from 'react';

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

  return response.json();
}

const getConversationbyId = async (id) => {
  const response = await fetch('http://localhost:8080/conversations/'+id);
  if (!response.ok) {
    throw new Error('Failed to fetch conversation');
  }
  return response.json();
}

const addMsgToConversation = async (conversationId, authorId, messageText) => {
  const response = await fetch('http://localhost:8080/conversations/addMsg/'+conversationId, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json'
    },
    body: JSON.stringify({authorId, messageText })
  });
  if (!response.ok) {
    throw new Error('Failed to add message to conversation');
  }
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

const MatchesList = ({onSelectMatch, matches}) => {

 const imagesPath = "http://localhost:8080/images/";

  return (
  <div className="rounded-lg shadow-lg p-4">
    <h2 className="text-2xl font-bold mb-4">Matches</h2>
    <ul>

   {
   matches.map(match => (
    <li key={match.id} className="mb-2"> 
    <button className="w-full hover:bg-gray-100 rounded flex item-center" onClick= {() => onSelectMatch(match)} >
        <img src={imagesPath+match.profile.imageUrl} className="w-16 h-16 rounded-full mr-3 object-cover"></img>
        <span>
          <h3 className='font-bold'>
            {match.profile.firstName} {match.profile.lastName}
          </h3>
        </span>
    </button> 
    </li>
   )
  )
   }

    </ul>
  </div>
  );
  }


const ChatScreen =    ({match, messagesList,refrechState}) => {
  const [input, setInput] = useState('');

  const handleSend = async (input) => {
    if (input.trim()) {
    
      let conversation = await addMsgToConversation(match.conversationId,"user",input);
      
       setInput('');

  //console.log('Dans handle :' +  JSON.stringify(conversation.messages));

       refrechState(conversation.messages);
    }
    
  }

  return (
  <><div className="rounded-lg shadow-lg p-4">
      <h2 className="text-2xl font-bold mb-2">Chat</h2>
      <h4 className="text-1xl font-bold mb-4">{match.profile.firstName} {match.profile.lastName}</h4>
    <div>  
  {messagesList.map((message,index) => (
      <div key={index}>
        <div>
          {message.messageText} 
        </div>
      </div>
      ))
  }
</div>

    </div><div className="flex items-center">
        <input
          type="text"
          value={input}
          onChange={(e) => setInput(e.target.value)}
          className="flex-1 border-2 border-gray-300 rounded-full py-2 px-4 mr-2 focus:outline-none focus:border-blue-500"
          placeholder="Type a message..." />
        <button
          className="bg-blue-500 text-white rounded-full p-2 hover:bg-blue-600 transition-colors duration-200"
          onClick={() =>{handleSend(input);}}
        >
          <Send size={24} />
        </button>
      </div></>
   );
}

function App() {

  const [currentScreen, setCurrentScreen] = useState('profile');
  const [currentProfile, setCurrentProfile] = useState(null);
  const [currentMatch, setCurrentMatch] = useState('match');
  const [currentMatches, setCurrentMatches] = useState([]);
  const [currentMessagesList, setCurrentMessagesList] = useState([]);

  const onSelectMatch =  async (match) => { 
  const conversations = await getConversationbyId(match.conversationId);
  setCurrentMessagesList(conversations.messages);
  setCurrentMatch(match);
  setCurrentScreen('chat');
  }    

  const onSwipe = async (profileId,direction) => {
    loadRandomProfile();
    
    if (direction === 'right') {
      await saveSwipe(profileId);
      await loadMatches();
    }

    if (direction === 'left') {
      console.log('Dislike');
    }

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
      setCurrentMatches(matches);

    } catch (error) {
      console.error(error);
    }
  } 

 const refrechChatState = (messages) => {

//console.log('Refrech Here' + messages)

  setCurrentMessagesList(messages);

  }

  const renderScreen = () => {

    switch (currentScreen) {
      case 'profile':
        return <ProfileSelector profile={currentProfile} onSwipe={onSwipe}/>;
      case 'matches':
        return <MatchesList  matches={currentMatches} onSelectMatch={onSelectMatch}/>;
      case 'chat':
          return <ChatScreen match={currentMatch} messagesList={currentMessagesList} refrechState={refrechChatState}/>;   
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

export default App;
