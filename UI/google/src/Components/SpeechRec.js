import React from 'react'
import SpeechRecognition, { useSpeechRecognition } from 'react-speech-recognition'

const SpeechRec = ({speechRecHandler}) => {
  const { transcript, resetTranscript } = useSpeechRecognition()

  if (!SpeechRecognition.browserSupportsSpeechRecognition()) {
      alert('your browser does not support Speech Recogonition')
    return null
  }
  function stopHandler(){
    speechRecHandler(transcript); 
    SpeechRecognition.stopListening();
    console.log("text after stop: "+localStorage.getItem('text'))
  }
  function resetHandler(){
    resetTranscript();
    speechRecHandler("");
  }
  return (
    <div className="mt-3">
      <button class="btn btn-primary m-2 " onClick={SpeechRecognition.startListening}>Listen</button>
      <button class="btn btn-primary m-2 " onClick={() => stopHandler()}>Stop</button>
      <button class="btn btn-secondary m-2" onClick={() => resetHandler()}>Reset</button>
      {/* <p>{transcript}</p> */}
    </div>
  )
}
export default SpeechRec