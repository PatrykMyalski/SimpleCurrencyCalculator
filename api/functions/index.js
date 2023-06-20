
const functions = require("firebase-functions");
const admin = require("firebase-admin");
const axios = require("axios");

admin.initializeApp();


/** Fetches data from an API and saves it to Firebase Firestore.

@return {Promise<void>} A Promise that resolves when the data is saved.

async function fetchDataAndSaveToFirestore() {
  try {
  // Make an API request to fetch data
  const response = await axios.get("https://api.currencyapi.com/v3/latest?apikey=DfHgL2ZN6L0kDaxrBKOwgDhYQeHIlyjkQ7R6dNMl&currencies=");
  // Process the received data
  const data = response.data;
  // Save data to Firebase Firestore
  const firestore = admin.firestore();
  const docRef = firestore.collection("data").doc();
  await docRef.set(data);
  console.log("Data is saved in firestore");
  } catch (error) {
  console.error("An error occurred while fetching data:", error);
  }
  }
  **/
async function fetchDataAndSaveToFirestore() {
  try {
    // Make api request
    const response = await axios.get("https://api.currencyapi.com/v3/latest?apikey=DfHgL2ZN6L0kDaxrBKOwgDhYQeHIlyjkQ7R6dNMl&currencies=");

    // Reading data
    const data = response.data;

    // Saving data in firestore
    const firestore = admin.firestore();
    const docRef = firestore.collection("data").doc("6uf5lDeR8fpW0TOntk3V");
    await docRef.set(data);

    console.log("Data is saved in firestore");
  } catch (error) {
    console.error("Exception occur:", error);
  }
}

// Invoke function everyday at 0.30 AM UTC
exports.scheduleFetchData = functions.pubsub
    .schedule("30 0 * * *")
    .timeZone("Etc/UTC")
    .onRun(async (context) => {
      // Invoke data getter
      await fetchDataAndSaveToFirestore();

      return null;
    });

