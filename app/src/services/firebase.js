// Import the functions you need from the SDKs you need
import { initializeApp } from "firebase/app";
import { getAnalytics } from "firebase/analytics";
// TODO: Add SDKs for Firebase products that you want to use
// https://firebase.google.com/docs/web/setup#available-libraries

// Your web app's Firebase configuration
// For Firebase JS SDK v7.20.0 and later, measurementId is optional
const firebaseConfig = {
  apiKey: "AIzaSyANbHMoB79Wec1vF0HbiUomuNUA2TjjtMo",
  authDomain: "projetoreceita-58b5b.firebaseapp.com",
  projectId: "projetoreceita-58b5b",
  storageBucket: "projetoreceita-58b5b.appspot.com",
  messagingSenderId: "498418484629",
  appId: "1:498418484629:web:43e064aeb6807399dba29c",
  measurementId: "G-LGREB3XW70"
};

// Initialize Firebase
const app = initializeApp(firebaseConfig);
const analytics = getAnalytics(app);