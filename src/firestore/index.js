import * as app from "firebase/app";
import "firebase/firestore";
import "firebase/database";
import "firebase/storage";

// const config =
//   process.env.NODE_ENV === 'development' ?
//   JSON.parse(process.env.VUE_APP_FIREBASE_CONFIG) :
//   JSON.parse(process.env.VUE_APP_FIREBASE_CONFIG_PUBLIC)

// Your web app's Firebase configuration
var firebaseConfig = {
  apiKey: "AIzaSyDOssJBK7T7jKB1-XStyxKyPKoZ4OUH668",
  authDomain: "absolute-nuance-183415.firebaseapp.com",
  databaseURL: "https://absolute-nuance-183415.firebaseio.com",
  projectId: "absolute-nuance-183415",
  storageBucket: "absolute-nuance-183415.appspot.com",
  messagingSenderId: "148877626854",
  appId: "1:148877626854:web:009965e2389eac9c289a27",
  measurementId: "G-X9QYYB490H"
};
// Initialize Firebase
app.initializeApp(firebaseConfig);
export const firebase = app;
export const db = app.firestore();
export const storageRef = app.storage().ref();

export const usersRef = db.collection("users");
export const roomsRef = db.collection("chatRooms");

export const filesRef = storageRef.child("files");

export const dbTimestamp = firebase.firestore.FieldValue.serverTimestamp();
export const deleteDbField = firebase.firestore.FieldValue.delete();

export async function existUser(_id) {
  const user = await usersRef.doc(_id).get();
  console.log(user, user.exists)
  return user.exists
}
export async function existRoom(roomid) {
  const room = await roomsRef.doc(roomid).get();
  return room.exists
}
export async function createUser(user) {
  await usersRef.doc(user._id).set(user);
}
