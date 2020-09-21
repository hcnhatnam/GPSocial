//
//  MapViewController.swift
//  QuickChat
//
//  Created by Do Le Duy on 9/19/20.
//  Copyright © 2020 Haik Aslanyan. All rights reserved.
//

import GoogleMaps
import UIKit
import GoogleMapsUtils
import AlamofireImage
import Alamofire

typealias LDMapPosition = CLLocationCoordinate2D

let kDebugTotalMarker = 100
let kCameraLatitude: CLLocationDegrees = 16.0
let kCameraLongitude: CLLocationDegrees = 106.0
let kDefaultCameraZoom: Float = 4.0

class MapViewController: UIViewController, GMSMapViewDelegate {
  
  private var googleMapView: GMSMapView!
  private var clusterManager: GMUClusterManager!
  private var ip2LocationService = IP2LocationService()
  
  private var conversations = [ObjectConversation]()
  private let manager = ConversationManager()
  private var currentUser: ObjectUser?
  private let userManager = UserManager()
  private let ownerMarker = GMSMarker()
  
  override func loadView() {
    /// Setup camera
    let camera = GMSCameraPosition.camera(withLatitude: kCameraLatitude, longitude: kCameraLongitude, zoom: kDefaultCameraZoom)
    
    /// Set googleMapView as viewController.view
    googleMapView = GMSMapView.map(withFrame: CGRect.zero, camera: camera)
    self.view = googleMapView
  }
  
  override func viewDidLoad() {
    super.viewDidLoad()
    setupGoogleMap()
    fetchProfile()
    fetchConversations()
  }
  
  override func viewWillAppear(_ animated: Bool) {
    super.viewWillAppear(animated)
    showNavigationBar(enableShow: true)
  }
    
  func setupGoogleMap() {
    /// Setup ClusterManager
    let iconGenerator = GMUDefaultClusterIconGenerator()
    let algorithm = GMUNonHierarchicalDistanceBasedAlgorithm()
    let renderer = GMUDefaultClusterRenderer(mapView: googleMapView, clusterIconGenerator: iconGenerator)
    clusterManager = GMUClusterManager(map: googleMapView, algorithm: algorithm, renderer: renderer)
    clusterManager.setMapDelegate(self)
    
    /// Mark owner location on GoogleMapView
    markRandom()
    
    /// Setup ownermarker
    ownerMarker.icon = GMSMarker.markerImage(with: .red)
    ownerMarker.map = googleMapView
    ownerMarker.iconView = UIImageView()
    ownerMarker.iconView?.frame = CGRect(x: 0, y: 0, width: 50, height: 50)
    ownerMarker.iconView?.layer.cornerRadius = 25.0
    ownerMarker.iconView?.clipsToBounds = true
    ownerMarker.tracksViewChanges = true
    markOwnerLocationOnMapView()
  }
  
  // MARK: - GMUMapViewDelegate
  
  func mapView(_ mapView: GMSMapView, didTap marker: GMSMarker) -> Bool {
    mapView.animate(toLocation: marker.position)
    if let _ = marker.userData as? GMUCluster {
      mapView.animate(toZoom: mapView.camera.zoom + 1)
      NSLog("Did tap cluster")
      return true
    }
    
    let vc: MessagesViewController = UIStoryboard.initial(storyboard: .messages)
    vc.conversation = conversations[0]
    vc.modalPresentationStyle = .fullScreen
    manager.markAsRead(conversations[0])
    show(vc, sender: self)
    
    NSLog("Did tap marker")
    return false
  }
  
  // MARK: - Private
  
  func markRandom() {
    for position in self.genRandomPositions(count: kDebugTotalMarker) {
      markOnMapViewWithPosition(position, .blue)
    }
  }
  
  func markOwnerLocationOnMapView() {
    ip2LocationService.loadOwnerIP { result in
      switch result {
      case .success(let ownerIP):
        self.ip2LocationService.getLocationOfIP(ip: ownerIP) { (result) in
          switch result {
          case .success(let position):
            self.ownerMarker.position = position
          case .failure(let error):
            self.showAlert(title: "Alert", message: error.localizedDescription, completion: nil)
          }
        }
      case .failure(let error):
        self.showAlert(title: "Alert", message: error.localizedDescription, completion: nil)
      }
    }
  }
  
  func markOnMapViewWithPosition(_ position: LDMapPosition, _ color: UIColor) {
    let marker = self.createMarkerFromPosition(position, color)
    marker.map = self.googleMapView
  }
}

// MARK: - Conversations

extension MapViewController {
  
  func fetchConversations() {
    manager.currentConversations {[weak self] conversations in
      self?.conversations = conversations.sorted(by: {$0.timestamp > $1.timestamp})
    }
  }
  
  func fetchProfile() {
    userManager.currentUserData {[weak self] user in
      self?.currentUser = user
      
      /// Update ownerMarker after fetch done currentUser
      if let urlString = user?.profilePicLink {
        self?.updateOwnerMarkerIconWithUrl(url: urlString)
      }
    }
  }
  
  func updateOwnerMarkerIconWithUrl(url: String) {
    (self.ownerMarker.iconView as! UIImageView).setImage(url: URL(string: url))
  }

}

// MARK: - Helper

extension MapViewController {
  
  func createMarkerFromPosition(_ position: LDMapPosition, _ color: UIColor) -> GMSMarker {
    let marker = GMSMarker(position: position)
    marker.icon = GMSMarker.markerImage(with: color)
    //marker.title = "This is title"
    
    return marker
  }
  
  func genRandomPositions(count: Int) -> [LDMapPosition] {
    var listPosition: [LDMapPosition] = []
    for _ in 1...count {
      listPosition.append(LDMapPosition(latitude: self.randomLatitude(), longitude: self.randomLongitude()))
    }
    return listPosition
  }
  
  func randomLatitude() -> CLLocationDegrees {
    return CLLocationDegrees(Int.random(in: -90...90))
  }
  
  func randomLongitude() -> CLLocationDegrees {
    return CLLocationDegrees(Int.random(in: -180...180))
  }
  
}
