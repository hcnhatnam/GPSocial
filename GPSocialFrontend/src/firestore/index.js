import * as app from "firebase/app";
import "firebase/firestore";
import "firebase/database";
import "firebase/storage";

// const config =
//   process.env.NODE_ENV === 'development' ?
//   JSON.parse(process.env.VUE_APP_FIREBASE_CONFIG) :
//   JSON.parse(process.env.VUE_APP_FIREBASE_CONFIG_PUBLIC)

// Your web app's Firebase configuration
// var firebaseConfig = {
//   apiKey: "AIzaSyDOssJBK7T7jKB1-XStyxKyPKoZ4OUH668",
//   authDomain: "absolute-nuance-183415.firebaseapp.com",
//   databaseURL: "https://absolute-nuance-183415.firebaseio.com",
//   projectId: "absolute-nuance-183415",
//   storageBucket: "absolute-nuance-183415.appspot.com",
//   messagingSenderId: "148877626854",
//   appId: "1:148877626854:web:009965e2389eac9c289a27",
//   measurementId: "G-X9QYYB490H"
// };

var firebaseConfig = {
  apiKey: "AIzaSyCHPJjbTdV-BavOsq5cwrBLa3wB03lLu5A",
  authDomain: "iplocationchat.firebaseapp.com",
  databaseURL: "https://iplocationchat.firebaseio.com",
  projectId: "iplocationchat",
  storageBucket: "iplocationchat.appspot.com",
  messagingSenderId: "137047191722",
  appId: "1:137047191722:web:98a8e62f16f5d37f8160d3",
  measurementId: "G-6YGR56W3ES"
};
// Initialize Firebase
app.initializeApp(firebaseConfig);
export const firebase = app;
export const db = app.firestore();
export const storageRef = app.storage().ref();

export const usersRef = db.collection("Users");
export const roomsRef = db.collection("Conversations");

export const filesRef = storageRef.child("files");

export const dbTimestamp = firebase.firestore.FieldValue.serverTimestamp();
export const deleteDbField = firebase.firestore.FieldValue.delete();

export async function findUserByEmail(email) {
  const user = await usersRef
    .where('email', '==', email.toLowerCase())
    .get()
    .then(querySnapshot => {
      if (!querySnapshot.empty) {
        const user = querySnapshot.docs[0].data()
        return user;
      } else {
        return null;
      }
    })
  return user
}
export async function findUser(_id) {
  const user = await usersRef.doc(_id).get();
  if (user.exists) {
    return user.data()
  }
  return null;
}
export async function existUser(_id) {
  const user = await findUser(_id);
  return user !== null
}
export async function existRoom(roomid) {
  const room = await roomsRef.doc(roomid).get();
  return room.exists
}
export async function createUser(user) {
  console.log("createUser", user)

  const {
    id
  } = await usersRef.add({
    username: user.name,
    avatar: user.avatar,
    email: user.email
  });
  await usersRef.doc(id).update({
    _id: id
  });
  return id;
}
