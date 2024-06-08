import { Component, OnInit, ViewChild } from '@angular/core';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';

import { Profile } from './../../models/Profile';
import { ProfileService } from './../../services/profile.service';
import { Router } from '@angular/router';
import { Rol } from 'src/app/models/Rol';
import Swal from 'sweetalert2';
import { UserService } from 'src/app/services/user.service';

@Component({
  selector: 'app-admin-profiles',
  templateUrl: './admin-profiles.component.html',
  styleUrls: ['./admin-profiles.component.scss']
})
export class AdminProfilesComponent implements OnInit {
  profile: Profile = null;

  profiles: Array<Profile>;
  profilesVO: Array<{ user:string, name: string, domain: string, raw:Rol}>

  displayedColumns: string[] = ['name', 'domain','rol', 'opt'];
  dataSource: MatTableDataSource<Profile>;

  totalResults: number;

  public paginatorSize: number = 10;
  public domain: string = "ADMIN_PROFILES";

  //TABLA
  @ViewChild(MatPaginator) paginator: MatPaginator;
  @ViewChild(MatSort, { static: true }) sort: MatSort;

  constructor(private router: Router,
    private profileService: ProfileService,
    private userService:UserService
  ) { }

  ngOnInit(): void {
    this.getAllProfiles();

    if (localStorage.getItem("tracking_pageSize") != null)
      this.paginatorSize = parseInt(localStorage.getItem("tracking_pageSize"));
    //this.loadProfile();
  }

  getAllProfiles() {
    this.profileService.getAllProfiles().subscribe(
      (res) => {
        this.profilesVO=[];
        this.profiles = res;
        if(this.profiles!=null){
          console.log("load");
          this.profiles.forEach((profile:Profile)=>{
            profile.roles.forEach((rol:Rol)=>{
              this.profilesVO.push({ 'user': profile.user, 'domain': rol.domain, 'name': rol.name ,'raw':rol});
            });
          });
        }
        this.loadPaginator(this.profilesVO);
      },
      (err) => {
        console.log(err);
      }
    );
  }

  deleteUser(profile) {
    Swal.fire({
      title: '¿Está seguro de eliminar el registro?',
      text: "¡No podrá revertir este cambio!",
      icon: 'warning',
      showCancelButton: true,
      confirmButtonColor: '#3085d6',
      cancelButtonColor: '#d33',
      confirmButtonText: 'Sí, eliminar registro!'
    }).then((result) => {
      if (result.value) {
        this.userService.deleteUser(profile.user,profile.raw.id).subscribe(
          (response) => {
            this.getAllProfiles();
            Swal.fire(
              '¡Registro borrado!',
              'El registro ha sido eliminado.',
              'success'
            )
          }, (error) => {
            console.log(error);
          }
        );
      }
    })
  }

  // NO SE ESTA UTILIZANDO RESTRICCIONES DE USUARIOS
  /*loadProfile() {
    let user = localStorage.getItem("currentUser");
    if (user == null || user == '') return;
    this.profileService.getFilteredProfileFor(user, this.domain).subscribe(
      (res) => {
        this.profile = <Profile>(res);
        if (res != null) {
          Object.setPrototypeOf(this.profile, Profile.prototype);
          this.profile.setRolesNature();
        }
        else {
          this.profile = new Profile();
          this.profile.user = user;
          this.profile.roles = [];
        }
        //si no hay roles, cargo el 'DEFAULT'
        if (this.profile.roles.length == 0) {
          let rol = new Rol();
          rol.domain = this.domain;
          rol.name = "Default";
          this.profile.roles = [rol];
        }
        console.log(res);

        if (this.mustEnableAccess() == false) {
          this.router.navigate(['app']);
        }

      },
      (err) => {
        console.log(err);
        this.router.navigate(['//']);
      });
  }*/

  // NO SE ESTA UTILIZANDO RESTRICCIONES DE USUARIOS
  /*mustEnableAccess(): boolean {
    if (this.profile == null || this.profile.roles == null) return false;

    for (var i = 0; i < this.profile.roles.length; i++) {
      if ((this.profile.roles[i].name.toUpperCase() === "EJECUTIVO") ||
        (this.profile.roles[i].name.toUpperCase() === "ADMINISTRADOR"))
        return true;
    }

    return false;
  }*/

  handlePaginatorEvent(event: any) {
    this.paginatorSize = event.pageSize;
    localStorage.setItem("tracking_pageSize", this.paginatorSize + "");
  }

  loadPaginator(data: any) {
    this.dataSource = new MatTableDataSource(data);
    this.dataSource.paginator = this.paginator;
    this.paginator._intl.itemsPerPageLabel = 'Registros por página.'
    this.dataSource.sort = this.sort;
  }

  applyFilter(event: Event) {
    const filterValue = (event.target as HTMLInputElement).value;
    this.dataSource.filter = filterValue.trim().toLowerCase();
    if (this.dataSource.paginator) {
      this.dataSource.paginator.firstPage();
    }
  }


}
